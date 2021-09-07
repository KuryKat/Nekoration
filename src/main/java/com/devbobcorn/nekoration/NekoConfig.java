package com.devbobcorn.nekoration;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class NekoConfig {
    public enum HorConnectionDir {
        LEFT2RIGHT,
        RIGHT2LEFT,
        BOTH,
        NEITHER
    }

    public enum VerConnectionDir {
        BOTTOM2TOP,
        TOP2BOTTOM,
        BOTH,
        NEITHER
    }

    public static class Client{
        public final ForgeConfigSpec.BooleanValue useImageRendering;
        public final ForgeConfigSpec.BooleanValue simplifyRendering;

        Client(ForgeConfigSpec.Builder builder){
            builder.comment("Painting renderer configuration settings").push("painting_renderer");
            this.useImageRendering =
                builder.comment("Whether to cache paintings to this client, and use them for rendering.(Default to true)")
                    .define("useImageRendering", true);
            this.simplifyRendering =
                builder.comment("Whether to simplify the lighting calculation when rendering paintings.(Default to true)")
                    .define("simplifyRendering", true);
            builder.pop();
        }
    }

    public static class Server{
        public final ForgeConfigSpec.EnumValue<HorConnectionDir> horConnectionDir;
        public final ForgeConfigSpec.EnumValue<VerConnectionDir> verConnectionDir;

        Server(ForgeConfigSpec.Builder builder){
            builder.comment("Block connection configuration settings").push("connection");
            this.horConnectionDir =
                builder.comment("In what placement order should horizontal blocks be connected to adjacent ones.(Default to LEFT2RIGHT)")
                    .defineEnum("horConnectionDir", HorConnectionDir.LEFT2RIGHT);
            this.verConnectionDir =
                builder.comment("In what placement order should vertical blocks be connected to adjacent ones.(Default to BOTTOM2TOP)")
                    .defineEnum("verConnectionDir", VerConnectionDir.BOTTOM2TOP);
            builder.pop();
        }
    }

    static final ForgeConfigSpec CLIENT_SPEC;
    public static final NekoConfig.Client CLIENT;

    static final ForgeConfigSpec SERVER_SPEC;
    public static final NekoConfig.Server SERVER;

    static {
        final Pair<NekoConfig.Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(NekoConfig.Client::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        final Pair<NekoConfig.Server, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(NekoConfig.Server::new);
        SERVER_SPEC = serverSpecPair.getRight();
        SERVER = serverSpecPair.getLeft();
    }
}
