package org.nting.flare.app.core;

import static org.nting.flare.playn.component.BoxFit.FIT_WIDTH;
import static org.nting.toolkit.component.builder.ContainerBuilder.panelBuilder;
import static org.nting.toolkit.layout.FormLayout.xy;

import java.util.List;

import org.nting.flare.java.animation.ActorAnimation;
import org.nting.flare.playn.component.FlareActorRenderObject;
import org.nting.toolkit.Component;
import org.nting.toolkit.app.IPageFactory;
import org.nting.toolkit.app.ListPageFactory;
import org.nting.toolkit.app.Pages;
import org.nting.toolkit.component.Panel;
import org.nting.toolkit.component.ScrollPane;
import org.nting.toolkit.component.builder.ContainerBuilder;
import org.nting.toolkit.event.MouseEvent;
import org.nting.toolkit.event.MouseListener;

public class FlareDemoPagesFactory extends ListPageFactory {

    public FlareDemoPagesFactory() {
        registerAnimationPage("Teddy", "flare/Teddy.flr", null, "idle");
        registerAnimationPage("Effects (Drop Shadow, Blur, Inner Shadow)", "flare/LayerEffects.flr", null, "idle");
        registerAnimationPage("Basketball", "flare/basketball_blur_effects.flr", null, "Untitled");
        registerAnimationPage("Penguin", "flare/Penguin.flr", null, "walk");
        registerAnimationCollectionPage("TrimPaths", "flare/trimpaths/", "LiquidDownload.flr", "ChomperFUIType.flr",
                "CircleDropLoader.flr", "PlayPause.flr", "SmileySwitch.flr", "SuccessCheck.flr", "Trim.flr",
                "WifiAnimation.flr");
    }

    private void registerAnimationPage(String pageName, String filePath, String artboardName, String animationName) {
        registerSubPage(pageName, () -> new IPageFactory() {

            @Override
            public Pages.PageSize getPageSize() {
                return Pages.PageSize.DOUBLE_COLUMN;
            }

            @Override
            public Component createContent(Pages pages) {
                return FlareRenderUtil.createContent(filePath, artboardName, animationName);
            }
        });
    }

    private void registerAnimationCollectionPage(String pageName, String filePath, String... fileNames) {
        registerSubPage(pageName, () -> new IPageFactory() {

            @Override
            public Pages.PageSize getPageSize() {
                return Pages.PageSize.DOUBLE_COLUMN;
            }

            @Override
            public Component createContent(Pages pages) {
                ContainerBuilder<Panel, ?> panelBuilder = panelBuilder(
                        "7dlu, center:0px:grow, 7dlu, center:0px:grow, 7dlu, center:0px:grow, 7dlu", "7dlu");
                for (int i = 0; i < fileNames.length; i++) {
                    if (i % 3 == 0) {
                        panelBuilder.formLayout().addRow("100dlu");
                    }

                    String fileName = fileNames[i];
                    panelBuilder.addComponent(FlareActorRenderObject.lazyFlareActorRenderObject(filePath + fileName,
                            null, null, flareActorRenderObject -> {
                                flareActorRenderObject.fit.setValue(FIT_WIDTH);
                                List<ActorAnimation> animations = flareActorRenderObject.getArtboard().animations();
                                if (0 < animations.size()) {
                                    flareActorRenderObject.setAnimation(animations.get(0));
                                }
                                flareActorRenderObject.addMouseListener(new MouseListener() {

                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        flareActorRenderObject.paused.adjustValue(p -> !p);
                                    }
                                });
                                flareActorRenderObject.setTooltipText(fileName);
                            }), xy((i % 3) * 2 + 1, panelBuilder.formLayout().lastRow()));

                    if (i % 3 == 2) {
                        panelBuilder.formLayout().addRow("7dlu");
                    }
                }

                return new ScrollPane(panelBuilder.build());
            }
        });
    }
}
