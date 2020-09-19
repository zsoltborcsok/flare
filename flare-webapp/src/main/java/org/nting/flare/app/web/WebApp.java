package org.nting.flare.app.web;

import org.nting.flare.app.core.FlareRenderUtil;
import org.nting.toolkit.app.ToolkitApp;
import org.nting.toolkit.layout.AbsoluteLayout;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import playn.core.PlayN;
import playn.html.HtmlGame;
import playn.html.HtmlGraphics;
import playn.html.HtmlPlatform;

public class WebApp extends HtmlGame {

    @Override
    public void start() {
        new Timer() {

            @Override
            public void run() {
                HtmlPlatform.register(new Configuration());
                setCanvasSize();
                preventNavigation();
                removeFontLoaderDivs();

                ToolkitApp.startApp()
                        .then(toolkitManager -> toolkitManager.root().addComponent(
                                FlareRenderUtil.createContent("flare/LayerEffects.flr", null, "idle"),
                                AbsoluteLayout.fillParentConstraint()));
            }
        }.schedule(400);
    }

    @Override
    protected void configureUncaughtExceptionHandler() {
        // GWT.setUncaughtExceptionHandler();
    }

    private void setCanvasSize() {
        Window.setMargin("0");

        ((HtmlGraphics) PlayN.graphics()).setSize(Window.getClientWidth(), Window.getClientHeight());
        Window.addResizeHandler(
                event -> ((HtmlGraphics) PlayN.graphics()).setSize(Window.getClientWidth(), Window.getClientHeight()));

        Document doc = Document.get();
        NodeList<Element> canvasElements = doc.getElementsByTagName("canvas");
        CanvasElement canvasElement = (CanvasElement) canvasElements.getItem(0);
        canvasElement.getStyle().setPosition(Style.Position.ABSOLUTE);
    }

    private void preventNavigation() {
        // Window.addWindowClosingHandler(event -> event.setMessage("Unsaved changes will be discarded!"));
    }

    private void removeFontLoaderDivs() {
        Document doc = Document.get();
        doc.getElementById("loadfont-bold").removeFromParent();
        doc.getElementById("loadfont-bolditalic").removeFromParent();
        doc.getElementById("loadfont-italic").removeFromParent();
        doc.getElementById("loadfont-regular").removeFromParent();
        doc.getElementById("loadfont-iconfont").removeFromParent();
    }

    // See: http://www.summa-tech.com/blog/2012/06/11/7-tips-for-exception-handling-in-gwt
    private static Throwable unwrap(Throwable throwable) {
        if (throwable instanceof UmbrellaException) {
            UmbrellaException umbrellaException = (UmbrellaException) throwable;
            if (umbrellaException.getCauses().size() == 1) {
                return unwrap(umbrellaException.getCauses().iterator().next());
            }
        }
        return throwable;
    }

    private static class Configuration extends HtmlPlatform.Config {

        private Configuration() {
            mode = HtmlPlatform.Mode.CANVAS;
            experimentalFullscreen = true;
        }
    }
}
