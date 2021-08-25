package com.devbobcorn.nekoration.entities;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.imageio.ImageIO;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.client.rendering.LocalImageLoader;
import com.devbobcorn.nekoration.utils.PixelPos;
import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class PaintingData {
    private short width;
    private short height;
    private int seed;

    public final boolean isClient;

    private int[] canvas;    // Fully Opaque, client-only
    private int[] pixels;    // Allows Transparency, for both sides
    private int[] composite; // The final painting, fully Opaque, client-only, for rendering

    private int paintingHash;// Client-only, used to get the corresponding PaintingImageRenderer of a painting

    public boolean imageReady = false;

    public PaintingData(short w, short h, boolean client, int seed){
        width = w;
        height = h;
        pixels = new int[w * h];
        this.seed = seed;
        isClient = client;
        if (isClient){
            Random random = new Random(seed); // Use the entity's id as a seed to ensure that each PaintingEntity's wooden frame is unique and constant...
            // Initialize canvas layer and composite layer as an empty canvas...
            canvas = new int[w * h];
            composite = new int[w * h];
            for (int i = 0;i < w;i++)
                for (int j = 0;j < h;j++)
                    composite[i + j * w] = canvas[i + j * w] = (i % 16 == 0 || j % 16 == 0) ? 0xBAA080 : 0xEAD6B0;
            int bottom = (h - 1) * w;
            int right = w - 1;
            for (int i = 0;i < w;i++){ // Wooden: 187 131 53 random range:30
                composite[i] = canvas[i] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                composite[i + bottom] = canvas[i + bottom] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
            for (int j = 1;j < h - 1;j++){
                composite[j * w] = canvas[j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                composite[right + j * w] = canvas[right + j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
            updatePaintingHash();
            cache();
        }
    }

    public PaintingData(short w, short h, int[] pix, boolean client, int seed){
        width = w;
        height = h;
        pixels = pix;
        this.seed = seed;
        isClient = client;
        if (isClient){
            Random random = new Random(seed); // Use the entity's id as a seed to ensure that each PaintingEntity's wooden frame is unique and constant...
            // Initialize the canvas layer as an empty canvas...
            canvas = new int[w * h];
            for (int i = 0;i < w;i++)
                for (int j = 0;j < h;j++)
                    canvas[i + j * w] = (i % 16 == 0 || j % 16 == 0) ? 0xBAA080 : 0xEAD6B0;
            int bottom = (h - 1) * w;
            int right = w - 1;
            for (int i = 0;i < w;i++){ // Wooden: 187 131 53 random range:30
                canvas[i] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                canvas[i + bottom] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
            for (int j = 1;j < h - 1;j++){
                canvas[j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
                canvas[right + j * w] = (172 + random.nextInt(30) << 16) + (116 + random.nextInt(30) << 8) + 38 + random.nextInt(18);
            }
            // Intialize the composite layer...
            composite = new int[w * h];
            recalculateComposite();
            updatePaintingHash();
            imageReady = cache();
        }
    }
    
    private static final String CACHE_PATH = "nekocache";

    public boolean cache(){
        // First check if the file's already cached
        Minecraft minecraft = Minecraft.getInstance();
        final File pathCheck = new File(minecraft.gameDirectory, CACHE_PATH);
        if (pathCheck.isDirectory()){
            final File fileCheck = new File(pathCheck, getPaintingHash() + ".png");
            if (fileCheck.exists()){
                System.out.println("Painting #" + getPaintingHash() + " already cached.");
                return true;
            }
        }
        return (imageReady = save(CACHE_PATH, String.valueOf(getPaintingHash()), true, false));
    }

    public boolean save(String path, String name, boolean composite, boolean showMessage){
        try {
            Minecraft minecraft = Minecraft.getInstance();
        
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0;i < width;i++)
                for (int j = 0;j < height;j++) {
                    // The composite layer does not contain alpha values, and we need to make it fully opaque here...
                    image.setRGB(i, j, composite ? 0xff000000 + getCompositeAt(i, j) : getPixelAt(i, j));
                }
            File folder = new File(minecraft.gameDirectory, path);
            if (!folder.exists() && !folder.mkdir())
                throw new IOException("Could not create folder");
            final File file = new File(folder, name + ".png");
            System.out.println("Painting cached to " + file.getAbsolutePath());
            if (!ImageIO.write(image, "png", file))
                throw new IOException("Could not encode image as png!");
            if (showMessage){
                MutableComponent component = new TextComponent(file.getName());
                component = component.withStyle(ChatFormatting.UNDERLINE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
                minecraft.player.displayClientMessage(new TranslatableComponent("gui.nekoration.message." + (composite ? "painting_saved" : "painting_content_saved"), component), false);
            }
            return true;
        } catch (IOException e) {
            imageReady = false;
            return false;
        }
    }

    public boolean load(String path, String name){
        Minecraft minecraft = Minecraft.getInstance();
        try {
            final File folder = new File(minecraft.gameDirectory, path);
            if (!folder.exists())
                throw new IOException("Could not find folder");
            
            final File file = new File(folder, name + ".png");
            if (!file.exists())
                throw new IOException("Could not find file");

            byte[] arr = LocalImageLoader.read(file.getAbsolutePath());
            NativeImage image = NativeImage.read(new ByteArrayInputStream(arr));
            for (int i = 0;i < Math.min(image.getWidth(), width);i++)
                for (int j = 0;j < Math.min(image.getHeight(), height);j++){
                    int color = image.getPixelRGBA(i, j);
                    pixels[j * width + i] = (color & 0xFF00FF00) + ((color & 0xFF0000) >> 16) + ((color & 0xFF) << 16);
                }
            recalculateComposite();
            System.out.println(String.format("Painting '%s' Loaded: %s x %s", name, image.getWidth(), image.getHeight()));
            return true;
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
            MutableComponent component = new TextComponent(name);
            minecraft.player.displayClientMessage(new TranslatableComponent("gui.nekoration.message.painting_load_failed", component), false);
            return false;
        }
    }

    public boolean clearCache(int target){
        Minecraft minecraft = Minecraft.getInstance();
        final File path = new File(minecraft.gameDirectory, CACHE_PATH);
        if (path.isDirectory()){
            final File file = new File(path, target + ".png");
            if (file.delete()){
                System.out.println("Painting #" + target + " cache cleared.");
                return true;
            }
        }
        return false;
    }

    private void updatePaintingHash(){
        paintingHash = Arrays.hashCode(composite);
    }

    public int getPaintingHash(){
        return paintingHash;
    }

    public static void writeTo(PaintingData data, CompoundTag tag){
        tag.putShort("Width", data.width);
        tag.putShort("Height", data.height);
        tag.putIntArray("Pixels", data.pixels);
        tag.putInt("Seed", data.seed);
    }

    public static PaintingData readFrom(CompoundTag tag){
        // Used on server to initialize a Painting...
        return new PaintingData(tag.getShort("Width"), tag.getShort("Height"), tag.getIntArray("Pixels"), false, tag.getInt("Seed"));
    }

    private boolean isLegal(int x, int y){
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public int[] getPixels(){
        return pixels;
    }

    public int[] getComposite(){
        return composite;
    }

    public void setPixels(int[] pixels){
        if (pixels.length == this.pixels.length)
            this.pixels = pixels;
        if (isClient)
            recalculateComposite();
    }
    
    public void setAreaPixels(byte partX, byte partY, byte partW, byte partH, int[] pixels){
        for (int i = 0;i < partW * 16;i++)
            for (int j = 0;j < partH * 16;j++) {
                int x = partX * 3 * 16 + i;
                int y = partY * 3 * 16 + j;
                if (!isLegal(x, y))
                    System.out.println(x + " 0.0 " + y);
                this.pixels[y * width + x] = pixels[j * partW * 16 + i];
                // Painting Coord.         => Part Coord.
            }
        if (isClient)
            recalculateComposite();
    }

    public void setPixel(int x, int y, int color){
        if (isLegal(x, y)){
            pixels[y * width + x] = color;
            //System.out.println("Pixel set @[" + x + ", " + y + "] Opacity: " + ((pixels[y * width + x] & 0xff000000)));
            //System.out.printf("%x\n", ((pixels[y * width + x] >> 24) & 0xff));
            if (isClient)
                recalculateCompositeAt(x, y);
        }
    }
    private static final int canvasize = 128;
    boolean[][] visited = new boolean[canvasize][canvasize];

    public int fill(int x, int y, int color, int opacity){
        if (!isLegal(x, y))
            return 0;

        for (int i = 0;i < canvasize;i++)
            for (int j = 0;j < canvasize;j++){
                visited[i][j] = false;
            }
        
        pixSearch(x, y);
        int cnt = 0;
        for (int i = 0;i < width;i++)
            for (int j = 0;j < height;j++) {
                if (visited[i][j]) {
                    cnt++;
                    pixels[i + j * width] = (opacity << 24) + color;
                }
            }
        recalculateComposite();
        return cnt;
    }

    private boolean checkAvailable(PixelPos pix){
        if (!isLegal(pix.x, pix.y))
            return false;
        return !visited[pix.x][pix.y];
    }

    private void setVisited(PixelPos pix){
        visited[pix.x][pix.y] = true;
    }

    private boolean checkColor(int origin, PixelPos pix){
        return origin == getCompositeAt(pix.x, pix.y);
    }

    private static final int[] offsetX = { 1,-1, 0, 0, 1,-1, 1,-1 };
    private static final int[] offsetY = { 0, 0, 1,-1, 1, 1,-1,-1 };
    private boolean connectDiagonal = false;

    private void pixSearch(int x, int y){
        final int originColor = getCompositeAt(x, y);
        // BFS...
        Queue<PixelPos> queue = new LinkedList<PixelPos>();
        queue.add(new PixelPos(x, y));
        visited[x][y] = true;
        
        while (!queue.isEmpty()){
            PixelPos pix = queue.poll();
            for (int i = 0;i < (connectDiagonal ? 8 : 4);i++){
                PixelPos tar = pix.offset(offsetX[i], offsetY[i]);

                if (checkAvailable(tar) && checkColor(originColor, tar)) {
                    queue.add(tar);
                    setVisited(tar);
                }
            }
        }
    }

    private void recalculateComposite(){
        for (int x = 0;x < width;x++)
            for (int y = 0;y < height;y++)
                composite[y * width + x] = NekoColors.getRGBColorBetween(((pixels[y * width + x] >> 24) & 0xff) / 255.0D, canvas[y * width + x] , pixels[y * width + x]);
        updatePaintingHash();
    }

    private void recalculateCompositeAt(int x, int y){
        // double opacity = (pixels[y * width + x] >> 24) / 255.0D;
        composite[y * width + x] = NekoColors.getRGBColorBetween(((pixels[y * width + x] >> 24) & 0xff) / 255.0D, canvas[y * width + x] , pixels[y * width + x]);
        updatePaintingHash();
    }

    public int getCompositeAt(int x, int y){
        return composite[x + y * width];
    }

    public int getPixelAt(int x, int y){
        return pixels[x + y * width];
    }

    public short getWidth(){
        return width;
    }

    public short getHeight(){
        return height;
    }
}