package mchorse.bbs.game.utils;

import mchorse.bbs.BBSData;
import mchorse.bbs.game.utils.manager.IManager;
import mchorse.bbs.game.utils.manager.data.AbstractData;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.animation.UIAnimationPanel;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.game.panels.UICraftingTablePanel;
import mchorse.bbs.ui.game.panels.UIDialoguePanel;
import mchorse.bbs.ui.game.panels.UIHUDScenePanel;
import mchorse.bbs.ui.game.panels.UIQuestChainPanel;
import mchorse.bbs.ui.game.panels.UIScriptPanel;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;
import mchorse.bbs.ui.recording.editor.UIRecordPanel;
import mchorse.bbs.ui.recording.scene.UIScenePanel;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;

import java.util.function.Function;
import java.util.function.Supplier;

public class ContentType
{
    public static final ContentType QUESTS = new ContentType("quests", UIKeys.OVERLAYS_QUEST, BBSData::getQuests, (dashboard) -> dashboard.getPanel(UIScenePanel.class));
    public static final ContentType CRAFTING_TABLES = new ContentType("crafting_tables", UIKeys.OVERLAYS_CRAFTING, BBSData::getCrafting, (dashboard) -> dashboard.getPanel(UICraftingTablePanel.class));
    public static final ContentType DIALOGUES = new ContentType("dialogues", UIKeys.OVERLAYS_DIALOGUE, BBSData::getDialogues, (dashboard) -> dashboard.getPanel(UIDialoguePanel.class));
    public static final ContentType CHAINS = new ContentType("chains", UIKeys.OVERLAYS_CHAIN, BBSData::getChains, (dashboard) -> dashboard.getPanel(UIQuestChainPanel.class));
    public static final ContentType SCRIPTS = new ContentType("scripts", UIKeys.OVERLAYS_SCRIPT, BBSData::getScripts, (dashboard) -> dashboard.getPanel(UIScriptPanel.class));
    public static final ContentType HUDS = new ContentType("huds", UIKeys.OVERLAY_HUDS, BBSData::getHUDs, (dashboard) -> dashboard.getPanel(UIHUDScenePanel.class));
    public static final ContentType CAMERAS = new ContentType("cameras", UIKeys.OVERLAYS_CAMERA, BBSData::getCameras, (dashboard) -> dashboard.getPanel(UICameraPanel.class));
    public static final ContentType SCENES = new ContentType("scenes", UIKeys.OVERLAYS_SCENE, BBSData::getScenes, (dashboard) -> dashboard.getPanel(UIScenePanel.class));
    public static final ContentType RECORDS = new ContentType("records", UIKeys.OVERLAYS_RECORD, BBSData::getRecords, (dashboard) -> dashboard.getPanel(UIRecordPanel.class));
    public static final ContentType ANIMATIONS = new ContentType("animations", UIKeys.OVERLAYS_ANIMATION, BBSData::getAnimations, (dashboard) -> dashboard.getPanel(UIAnimationPanel.class));
    public static final ContentType UIS = new ContentType("uis", UIKeys.OVERLAYS_UIS, BBSData::getUIs, (dashboard) -> dashboard.getPanel(UIUserInterfacePanel.class));
    public static final ContentType PARTICLES = new ContentType("particles", UIKeys.OVERLAYS_PARTICLE_EFFECT, BBSData::getParticles, (dashboard) -> dashboard.getPanel(UIParticleSchemePanel.class));

    private final String id;
    private IKey label;
    private Supplier<IManager<? extends AbstractData>> manager;
    private Function<UIDashboard, UIDataDashboardPanel> dashboardPanel;

    public ContentType(String id, IKey label, Supplier<IManager<? extends AbstractData>> manager, Function<UIDashboard, UIDataDashboardPanel> dashboardPanel)
    {
        this.id = id;
        this.label = label;
        this.manager = manager;
        this.dashboardPanel = dashboardPanel;
    }

    public String getId()
    {
        return this.id;
    }

    public IKey getPickLabel()
    {
        return this.label;
    }

    /* Every Karen be like :D */
    public IManager<? extends AbstractData> getManager()
    {
        return this.manager.get();
    }

    public UIDataDashboardPanel get(UIDashboard dashboard)
    {
        return this.dashboardPanel.apply(dashboard);
    }
}