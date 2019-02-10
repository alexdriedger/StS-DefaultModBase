package theDefault.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.Apotheosis;
import com.megacrit.cardcrawl.cards.colorless.JAX;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import theDefault.DefaultMod;
import theDefault.relics.BottledPlaceholderRelic;

import static theDefault.DefaultMod.makeEventPath;

public class IdentityCrisisEvent extends AbstractImageEvent {


    public static final String ID = DefaultMod.makeID("IdentityCrisisEvent");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);

    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    public static final String IMG = makeEventPath("IdentityCrisisEvent.png");

    private int screenNum = 0; // The initial screen we will see when encountering the event - screen 0;

    private float HEALTH_LOSS_PERCENTAGE = 0.03F; // 3%
    private float HEALTH_LOSS_PERCENTAGE_LOW_ASCENSION = 0.05F; // 5%

    private int healthdamage; //The actual number of how much Max HP we're going to lose.

    public IdentityCrisisEvent() {
        super(NAME, DESCRIPTIONS[0], IMG);

        if (AbstractDungeon.ascensionLevel >= 15) { // If the player is ascension 15 or above, lose 5% max hp. Else, lose just 3%.
            healthdamage = (int) ((float) AbstractDungeon.player.maxHealth * HEALTH_LOSS_PERCENTAGE);
        } else {
            healthdamage = (int) ((float) AbstractDungeon.player.maxHealth * HEALTH_LOSS_PERCENTAGE_LOW_ASCENSION);
        }

        // The first dialogue options available to us.
        imageEventText.setDialogOption(OPTIONS[0]); // Inspiration - Gain a Random Starting Relic
        imageEventText.setDialogOption(OPTIONS[1] + healthdamage + OPTIONS[2]); // Denial - lose healthDamage Max HP
        imageEventText.setDialogOption(OPTIONS[3], new Apotheosis()); // Acceptance - Gain Apotheosis
        imageEventText.setDialogOption(OPTIONS[4]); // TOUCH THE MIRROR
    }

    @Override
    protected void buttonEffect(int i) { // This is the event:
        switch (screenNum) {
            case 0: // While you are on screen number 0 (The starting screen)
                switch (i) {
                    case 0: // If you press button the first button (Button at index 0), in this case: Inspiration.
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]); // Update the text of the event
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]); // 1. Change the first button to the [Leave] button
                        this.imageEventText.clearRemainingOptions(); // 2. and remove all others
                        screenNum = 1; // Screen set the screen number to 1. Once we exit the switch (i) statement,
                        // we'll still continue the switch (screenNum) statement. It'll find screen 1 and do it's actions
                        // (in our case, that's the final screen, but you can chain as many as you want like that)

                        AbstractRelic relicToAdd = RelicLibrary.starterList.get(AbstractDungeon.cardRandomRng.random(RelicLibrary.starterList.size() - 1));
                        // Get a random starting relic

                        relicToAdd.instantObtain(AbstractDungeon.player, 0, false); // Obtain it
                        relicToAdd.playLandingSFX(); // Play it's obtain sound effect
                        relicToAdd.flash(); // and make it flash

                        break; // Onto screen 1 we go.
                    case 1: // If you press button the first button (Button at index 0), in this case: Deinal
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature) null, healthdamage));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new JAX(), (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));

                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature) null, damageMedium));
                        AbstractCard c = AbstractDungeon.getCard(AbstractCard.CardRarity.UNCOMMON, AbstractDungeon.cardRng).makeCopy();
                        c.upgrade();
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                        break;
                    case 2: // If you press button the first button (Button at index 0), in this case: Acceptance
                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature) null, healthdamage));
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new JAX(), (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));

                        AbstractDungeon.player.damage(new DamageInfo((AbstractCreature) null, damageHigh));
                        AbstractDungeon.player.relics.add(new BottledPlaceholderRelic());
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                        break;
                    case 3: // If you press button the first button (Button at index 0), in this case: TOUCH
                        imageEventText.loadImage("theDefaultResources/images/events/IdentityCrisisEvent2.png");
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[4]);
                        this.imageEventText.clearRemainingOptions();
                        screenNum = 1;
                        break;
                }
                break;
            case 1:
                switch (i) {
                    case 0:
                        openMap();
                        break;
                }
                break;
        }
    }
}