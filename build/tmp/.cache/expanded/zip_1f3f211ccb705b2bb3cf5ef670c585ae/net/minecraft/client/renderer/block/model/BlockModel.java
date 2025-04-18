package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BlockModel implements UnbakedModel {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    @VisibleForTesting
    static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(BlockModel.class, new BlockModel.Deserializer())
        .registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer())
        .registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer())
        .registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer())
        .registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer())
        .registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer())
        .registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer())
        .create();
    private static final char REFERENCE_CHAR = '#';
    public static final String PARTICLE_TEXTURE_REFERENCE = "particle";
    private static final boolean DEFAULT_AMBIENT_OCCLUSION = true;
    private final List<BlockElement> elements;
    @Nullable
    private final BlockModel.GuiLight guiLight;
    @Nullable
    public final Boolean hasAmbientOcclusion;
    private final ItemTransforms transforms;
    private final List<ItemOverride> overrides;
    public String name = "";
    @VisibleForTesting
    public final Map<String, Either<Material, String>> textureMap;
    @Nullable
    public BlockModel parent;
    @Nullable
    protected ResourceLocation parentLocation;
    public final net.minecraftforge.client.model.geometry.BlockGeometryBakingContext customData = new net.minecraftforge.client.model.geometry.BlockGeometryBakingContext(this);

    public static BlockModel fromStream(Reader p_111462_) {
        return GsonHelper.fromJson(net.minecraftforge.client.model.ExtendedBlockModelDeserializer.INSTANCE, p_111462_, BlockModel.class);
    }

    public static BlockModel fromString(String p_111464_) {
        return fromStream(new StringReader(p_111464_));
    }

    public BlockModel(
        @Nullable ResourceLocation p_273263_,
        List<BlockElement> p_272668_,
        Map<String, Either<Material, String>> p_272821_,
        @Nullable Boolean p_272676_,
        @Nullable BlockModel.GuiLight p_273072_,
        ItemTransforms p_273480_,
        List<ItemOverride> p_273099_
    ) {
        this.elements = p_272668_;
        this.hasAmbientOcclusion = p_272676_;
        this.guiLight = p_273072_;
        this.textureMap = p_272821_;
        this.parentLocation = p_273263_;
        this.transforms = p_273480_;
        this.overrides = p_273099_;
    }

    @Deprecated
    public List<BlockElement> getElements() {
        if (customData.hasCustomGeometry()) return java.util.Collections.emptyList();
        return this.elements.isEmpty() && this.parent != null ? this.parent.getElements() : this.elements;
    }

    @Nullable
    public ResourceLocation getParentLocation() {
        return parentLocation;
    }

    public boolean hasAmbientOcclusion() {
        if (this.hasAmbientOcclusion != null) {
            return this.hasAmbientOcclusion;
        } else {
            return this.parent != null ? this.parent.hasAmbientOcclusion() : true;
        }
    }

    public BlockModel.GuiLight getGuiLight() {
        if (this.guiLight != null) {
            return this.guiLight;
        } else {
            return this.parent != null ? this.parent.getGuiLight() : BlockModel.GuiLight.SIDE;
        }
    }

    public boolean isResolved() {
        return this.parentLocation == null || this.parent != null && this.parent.isResolved();
    }

    public List<ItemOverride> getOverrides() {
        return this.overrides;
    }

    private ItemOverrides getItemOverrides(ModelBaker p_250138_, BlockModel p_251800_) {
        return this.overrides.isEmpty() ? ItemOverrides.EMPTY : new ItemOverrides(p_250138_, p_251800_, this.overrides);
    }

    public ItemOverrides getOverrides(ModelBaker p_250138_, BlockModel p_251800_, Function<Material, TextureAtlasSprite> spriteGetter) {
       return this.overrides.isEmpty() ? ItemOverrides.EMPTY : new ItemOverrides(p_250138_, p_251800_, this.overrides, spriteGetter);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        Set<ResourceLocation> set = Sets.newHashSet();

        for (ItemOverride itemoverride : this.overrides) {
            set.add(itemoverride.getModel());
        }

        if (this.parentLocation != null) {
            set.add(this.parentLocation);
        }

        return set;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> p_249059_) {
        Set<UnbakedModel> set = Sets.newLinkedHashSet();

        for (BlockModel blockmodel = this; blockmodel.parentLocation != null && blockmodel.parent == null; blockmodel = blockmodel.parent) {
            set.add(blockmodel);
            UnbakedModel unbakedmodel = p_249059_.apply(blockmodel.parentLocation);
            if (unbakedmodel == null) {
                LOGGER.warn("No parent '{}' while loading model '{}'", this.parentLocation, blockmodel);
            }

            if (set.contains(unbakedmodel)) {
                LOGGER.warn(
                    "Found 'parent' loop while loading model '{}' in chain: {} -> {}",
                    blockmodel,
                    set.stream().map(Object::toString).collect(Collectors.joining(" -> ")),
                    this.parentLocation
                );
                unbakedmodel = null;
            }

            if (unbakedmodel == null) {
                blockmodel.parentLocation = ModelBakery.MISSING_MODEL_LOCATION;
                unbakedmodel = p_249059_.apply(blockmodel.parentLocation);
            }

            if (!(unbakedmodel instanceof BlockModel)) {
                throw new IllegalStateException("BlockModel parent has to be a block model.");
            }

            blockmodel.parent = (BlockModel)unbakedmodel;
        }

        if (customData.hasCustomGeometry()) {
            customData.getCustomGeometry().resolveParents(p_249059_, customData);
        }

        this.overrides.forEach(p_247932_ -> {
            UnbakedModel unbakedmodel1 = p_249059_.apply(p_247932_.getModel());
            if (!Objects.equals(unbakedmodel1, this)) {
                unbakedmodel1.resolveParents(p_249059_);
            }
        });
    }

    /**
     * @deprecated Forge: Use {@link #bake(ModelBaker, BlockModel, Function, ModelState, ResourceLocation, boolean)}.
     */
    @Deprecated
    @Override
    public BakedModel bake(ModelBaker p_252120_, Function<Material, TextureAtlasSprite> p_250023_, ModelState p_251130_) {
        return this.bake(p_252120_, this, p_250023_, p_251130_, true);
    }

    public BakedModel bake(ModelBaker p_249720_, BlockModel p_111451_, Function<Material, TextureAtlasSprite> p_111452_, ModelState p_111453_, boolean p_111455_) {
        return net.minecraftforge.client.model.geometry.UnbakedGeometryHelper.bake(this, p_249720_, p_111451_, p_111452_, p_111453_, p_111455_);
    }

    public BakedModel bake(
        ModelBaker p_249720_, BlockModel p_111451_, Function<Material, TextureAtlasSprite> p_111452_, ModelState p_111453_, boolean p_111455_, net.minecraftforge.client.RenderTypeGroup renderTypes
    ) {
        TextureAtlasSprite textureatlassprite = p_111452_.apply(this.getMaterial("particle"));
        if (this.getRootModel() == ModelBakery.BLOCK_ENTITY_MARKER) {
            return new BuiltInModel(this.getTransforms(), this.getItemOverrides(p_249720_, p_111451_), textureatlassprite, this.getGuiLight().lightLikeBlock());
        } else {
            SimpleBakedModel.Builder simplebakedmodel$builder = new SimpleBakedModel.Builder(this, this.getItemOverrides(p_249720_, p_111451_), p_111455_)
                .particle(textureatlassprite);

            for (BlockElement blockelement : this.getElements()) {
                for (Direction direction : blockelement.faces.keySet()) {
                    BlockElementFace blockelementface = blockelement.faces.get(direction);
                    TextureAtlasSprite textureatlassprite1 = p_111452_.apply(this.getMaterial(blockelementface.texture()));
                    if (blockelementface.cullForDirection() == null) {
                        simplebakedmodel$builder.addUnculledFace(bakeFace(blockelement, blockelementface, textureatlassprite1, direction, p_111453_));
                    } else {
                        simplebakedmodel$builder.addCulledFace(
                            Direction.rotate(p_111453_.getRotation().getMatrix(), blockelementface.cullForDirection()),
                            bakeFace(blockelement, blockelementface, textureatlassprite1, direction, p_111453_)
                        );
                    }
                }
            }

            return simplebakedmodel$builder.build();
        }
    }

    public static BakedQuad bakeFace(
        BlockElement p_111438_, BlockElementFace p_111439_, TextureAtlasSprite p_111440_, Direction p_111441_, ModelState p_111442_
    ) {
        return FACE_BAKERY.bakeQuad(
            p_111438_.from, p_111438_.to, p_111439_, p_111440_, p_111441_, p_111442_, p_111438_.rotation, p_111438_.shade
        );
    }

    public boolean hasTexture(String p_111478_) {
        return !MissingTextureAtlasSprite.getLocation().equals(this.getMaterial(p_111478_).texture());
    }

    public Material getMaterial(String p_111481_) {
        if (isTextureReference(p_111481_)) {
            p_111481_ = p_111481_.substring(1);
        }

        List<String> list = Lists.newArrayList();

        while (true) {
            Either<Material, String> either = this.findTextureEntry(p_111481_);
            Optional<Material> optional = either.left();
            if (optional.isPresent()) {
                return optional.get();
            }

            p_111481_ = either.right().get();
            if (list.contains(p_111481_)) {
                LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", Joiner.on("->").join(list), p_111481_, this.name);
                return new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
            }

            list.add(p_111481_);
        }
    }

    private Either<Material, String> findTextureEntry(String p_111486_) {
        for (BlockModel blockmodel = this; blockmodel != null; blockmodel = blockmodel.parent) {
            Either<Material, String> either = blockmodel.textureMap.get(p_111486_);
            if (either != null) {
                return either;
            }
        }

        return Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation()));
    }

    static boolean isTextureReference(String p_111489_) {
        return p_111489_.charAt(0) == '#';
    }

    public BlockModel getRootModel() {
        return this.parent == null ? this : this.parent.getRootModel();
    }

    public ItemTransforms getTransforms() {
        ItemTransform itemtransform = this.getTransform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        ItemTransform itemtransform1 = this.getTransform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        ItemTransform itemtransform2 = this.getTransform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
        ItemTransform itemtransform3 = this.getTransform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
        ItemTransform itemtransform4 = this.getTransform(ItemDisplayContext.HEAD);
        ItemTransform itemtransform5 = this.getTransform(ItemDisplayContext.GUI);
        ItemTransform itemtransform6 = this.getTransform(ItemDisplayContext.GROUND);
        ItemTransform itemtransform7 = this.getTransform(ItemDisplayContext.FIXED);

        var builder = com.google.common.collect.ImmutableMap.<ItemDisplayContext, ItemTransform>builder();
        for (ItemDisplayContext type : ItemDisplayContext.values()) {
           if (type.isModded()) {
              var transform = this.getTransform(type);
              if (transform != ItemTransform.NO_TRANSFORM) {
                 builder.put(type, transform);
              }
           }
        }

        return new ItemTransforms(itemtransform, itemtransform1, itemtransform2, itemtransform3, itemtransform4, itemtransform5, itemtransform6, itemtransform7, builder.build());
    }

    private ItemTransform getTransform(ItemDisplayContext p_270662_) {
        return this.parent != null && !this.transforms.hasTransform(p_270662_) ? this.parent.getTransform(p_270662_) : this.transforms.getTransform(p_270662_);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Deserializer implements JsonDeserializer<BlockModel> {
        public BlockModel deserialize(JsonElement p_111498_, Type p_111499_, JsonDeserializationContext p_111500_) throws JsonParseException {
            JsonObject jsonobject = p_111498_.getAsJsonObject();
            List<BlockElement> list = this.getElements(p_111500_, jsonobject);
            String s = this.getParentName(jsonobject);
            Map<String, Either<Material, String>> map = this.getTextureMap(jsonobject);
            Boolean obool = this.getAmbientOcclusion(jsonobject);
            ItemTransforms itemtransforms = ItemTransforms.NO_TRANSFORMS;
            if (jsonobject.has("display")) {
                JsonObject jsonobject1 = GsonHelper.getAsJsonObject(jsonobject, "display");
                itemtransforms = p_111500_.deserialize(jsonobject1, ItemTransforms.class);
            }

            List<ItemOverride> list1 = this.getOverrides(p_111500_, jsonobject);
            BlockModel.GuiLight blockmodel$guilight = null;
            if (jsonobject.has("gui_light")) {
                blockmodel$guilight = BlockModel.GuiLight.getByName(GsonHelper.getAsString(jsonobject, "gui_light"));
            }

            ResourceLocation resourcelocation = s.isEmpty() ? null : ResourceLocation.parse(s);
            return new BlockModel(resourcelocation, list, map, obool, blockmodel$guilight, itemtransforms, list1);
        }

        protected List<ItemOverride> getOverrides(JsonDeserializationContext p_111495_, JsonObject p_111496_) {
            List<ItemOverride> list = Lists.newArrayList();
            if (p_111496_.has("overrides")) {
                for (JsonElement jsonelement : GsonHelper.getAsJsonArray(p_111496_, "overrides")) {
                    list.add(p_111495_.deserialize(jsonelement, ItemOverride.class));
                }
            }

            return list;
        }

        private Map<String, Either<Material, String>> getTextureMap(JsonObject p_111510_) {
            ResourceLocation resourcelocation = TextureAtlas.LOCATION_BLOCKS;
            Map<String, Either<Material, String>> map = Maps.newHashMap();
            if (p_111510_.has("textures")) {
                JsonObject jsonobject = GsonHelper.getAsJsonObject(p_111510_, "textures");

                for (Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                    map.put(entry.getKey(), parseTextureLocationOrReference(resourcelocation, entry.getValue().getAsString()));
                }
            }

            return map;
        }

        private static Either<Material, String> parseTextureLocationOrReference(ResourceLocation p_111504_, String p_111505_) {
            if (BlockModel.isTextureReference(p_111505_)) {
                return Either.right(p_111505_.substring(1));
            } else {
                ResourceLocation resourcelocation = ResourceLocation.tryParse(p_111505_);
                if (resourcelocation == null) {
                    throw new JsonParseException(p_111505_ + " is not valid resource location");
                } else {
                    return Either.left(new Material(p_111504_, resourcelocation));
                }
            }
        }

        private String getParentName(JsonObject p_111512_) {
            return GsonHelper.getAsString(p_111512_, "parent", "");
        }

        @Nullable
        protected Boolean getAmbientOcclusion(JsonObject p_273052_) {
            return p_273052_.has("ambientocclusion") ? GsonHelper.getAsBoolean(p_273052_, "ambientocclusion") : null;
        }

        protected List<BlockElement> getElements(JsonDeserializationContext p_111507_, JsonObject p_111508_) {
            List<BlockElement> list = Lists.newArrayList();
            if (p_111508_.has("elements")) {
                for (JsonElement jsonelement : GsonHelper.getAsJsonArray(p_111508_, "elements")) {
                    list.add(p_111507_.deserialize(jsonelement, BlockElement.class));
                }
            }

            return list;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum GuiLight {
        FRONT("front"),
        SIDE("side");

        private final String name;

        private GuiLight(final String p_111525_) {
            this.name = p_111525_;
        }

        public static BlockModel.GuiLight getByName(String p_111528_) {
            for (BlockModel.GuiLight blockmodel$guilight : values()) {
                if (blockmodel$guilight.name.equals(p_111528_)) {
                    return blockmodel$guilight;
                }
            }

            throw new IllegalArgumentException("Invalid gui light: " + p_111528_);
        }

        public boolean lightLikeBlock() {
            return this == SIDE;
        }

        public String getSerializedName() {
            return name;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class LoopException extends RuntimeException {
        public LoopException(String p_173424_) {
            super(p_173424_);
        }
    }
}
