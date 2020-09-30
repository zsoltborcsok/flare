package org.nting.flare.app.core;

import org.nting.toolkit.Component;
import org.nting.toolkit.app.IPageFactory;
import org.nting.toolkit.app.ListPageFactory;
import org.nting.toolkit.app.Pages;

public class FlareDemoPagesFactory extends ListPageFactory {

    public FlareDemoPagesFactory() {
        registerAnimationPage("Teddy", "flare/Teddy.flr", null, "idle");
        registerAnimationPage("Effects (Drop Shadow, Blur, Inner Shadow)", "flare/LayerEffects.flr", null, "idle");
        registerAnimationPage("Basketball", "flare/basketball_blur_effects.flr", null, "Untitled");
        registerAnimationPage("Penguin", "flare/Penguin.flr", null, "walk");
        registerAnimationPage("TrimPaths", "flare/trimpaths/Liquid_Download.flr", null, "Demo");
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
}
