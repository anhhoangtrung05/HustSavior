package io.github.HustSavior.skills;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import io.github.HustSavior.entities.Player;

public class Slash extends Sprite implements Skills{

    TextureRegion[] animation;
    Animation<TextureRegion> cast;
    CooldownController cd;
    final static float DEFAULT_COOLDOWN=2.0f;
    private float stateTime=0;
    private float getAnimationTime;

    private World world;
    public Body hitbox;

    private float castingX;
    private float castingY;
    private boolean castDirectionLeft;
    Player player;

    public Slash(Sprite sprite, Player player, World world){
        super(sprite);
        this.cd=new CooldownController(DEFAULT_COOLDOWN);
        this.player=player;
        this.world=world;

        cast=createAnimation();
        getAnimationTime=cast.getAnimationDuration();
    }
    @Override
    public Animation<TextureRegion> createAnimation(){
        animation= new TextureRegion[3];
        animation[0]= new TextureRegion(new Texture("skills/Slash1.png"));
        animation[1]= new TextureRegion(new Texture("skills/Slash2.png"));
        animation[2]= new TextureRegion(new Texture("skills/Slash3.png"));
        //animation[3]= new TextureRegion(new Texture("skills/parabol4.png"));
        //animation[4]= new TextureRegion(new Texture("skills/parabol5.png"));
        //animation[5]= new TextureRegion(new Texture("skills/parabol6.png"));
        //animation[6]= new TextureRegion(new Texture("skills/parabol7.png"));

        return new Animation<TextureRegion>(1/60f, animation);
    }
    @Override
    public void draw(SpriteBatch batch){
        super.draw(batch);
    }
    @Override
    public void update(float delta) {
        // Start animation if cooldown is ready
        if (isReady()) {
            // Capture the player's direction at the start of the skill
            if (stateTime == 0) {
                castDirectionLeft = player.isFacingLeft(); // Store the facing direction
                if (!castDirectionLeft) castingX = player.getX() + player.getRegionWidth() / 2;
                else castingX= player.getX() - 3 * player.getRegionWidth()/2;
                castingY = player.getY();
                hitbox = createHitbox((int) castingX, (int) castingY, player.getPPM(), world);
            }

            // Get the current frame of the animation
            TextureRegion frame = cast.getKeyFrame(stateTime, false);

            // Flip the frame based on the captured direction
            if (castDirectionLeft && !frame.isFlipX()) {
                frame.flip(true, false);
            }
            else if (!castDirectionLeft && frame.isFlipX()){
                frame.flip(true, false);
            }

            setRegion(frame); // Set the flipped or unflipped frame to the sprite

            setPosition(castingX, castingY);

            // Check if animation has finished
            if (stateTime >= getAnimationTime) {
                cd.resetCooldown(); // Reset cooldown only after animation completes
                if (hitbox != null) {
                    world.destroyBody(hitbox);
                    hitbox = null;
                }
                stateTime = 0;// Reset stateTime for the next animation cycle
                if(frame.isFlipX()) flip(true, false);
            } else {
                stateTime += delta;
            }

        } else {
            cd.cooldownTimer(delta); // Update cooldown
        }
    }
    @Override
    public Body createHitbox(int x, int y, float PPM, World world){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x+getRegionWidth()/2f)/PPM, (y+getRegionHeight()/2f)/PPM);

        Body ItemBody = world.createBody(bodyDef);
        PolygonShape shape=new PolygonShape();
        shape.setAsBox(getRegionWidth()/2.0f/PPM, getRegionHeight()/2.0f/PPM);

        FixtureDef fixtureDef= new FixtureDef();
        fixtureDef.shape=shape;
        fixtureDef.isSensor=true;

        ItemBody.createFixture(fixtureDef).setUserData(this);
        shape.dispose();

        return ItemBody;
    }

    public boolean isReady(){
        return cd.isReady();
    }
}
