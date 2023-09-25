package be.sky.timeluncher.ui.panels.pages;

import be.sky.timeluncher.Launcher;
import be.sky.timeluncher.ui.PanelManager;
import be.sky.timeluncher.ui.panel.IPanel;
import be.sky.timeluncher.ui.panel.Panel;
import be.sky.timeluncher.ui.panels.pages.content.*;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;

import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
public class App extends Panel {
    GridPane sidemenu = new GridPane();
    GridPane navContent = new GridPane();

    private BackgroundImage backgroundImage;
    Node activeLink = null;
    ContentPanel currentPage = null;

    Button homeBtn, settingsBtn, holyBtn,alltheBtn;

    Saver saver = Launcher.getInstance().getSaver();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/app.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("app-layout");
        setCanTakeAllSize(this.layout);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(350);
        columnConstraints.setMaxWidth(350);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());

        // Side menu
        this.layout.add(sidemenu, 0, 0);
        sidemenu.getStyleClass().add("sidemenu");
        setLeft(sidemenu);
        setCenterH(sidemenu);
        setCenterV(sidemenu);

        // Background Image
        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 1, 0);

        // Nav content
        this.layout.add(navContent, 1, 0);
        setLeft(navContent);
        setCenterH(navContent);
        setCenterV(navContent);

        /*
         * Side menu
         */
        // Titre
        Label title = new Label("Time Launcher");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 30f));
        title.getStyleClass().add("home-title");
        setCenterH(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setTranslateY(30d);
        sidemenu.getChildren().add(title);

        // Navigation
        holyBtn = new Button("HolyTime");
        holyBtn.getStyleClass().add("sidemenu-nav-btn");
        MaterialDesignIconView<MaterialDesignIcon.M> iconView = new MaterialDesignIconView<>(MaterialDesignIcon.M.MINECRAFT);
        iconView.getStyleClass().add("material-design-icon-view");
        holyBtn.setGraphic(iconView);
        setCanTakeAllSize(holyBtn);
        setTop(holyBtn);
        holyBtn.setTranslateY(90d);
        holyBtn.setOnMouseClicked(e -> setPage(new Holytime(), holyBtn));

        homeBtn = new Button("Pixelmon");
        homeBtn.getStyleClass().add("sidemenu-nav-btn");
        MaterialDesignIconView<MaterialDesignIcon.P> iconViews = new MaterialDesignIconView<>(MaterialDesignIcon.P.POKEBALL);
        iconViews.getStyleClass().add("material-design-icon-view");
        homeBtn.setGraphic(iconViews);
        setCanTakeAllSize(homeBtn);
        setTop(homeBtn);
        homeBtn.setTranslateY(130d);
        homeBtn.setOnMouseClicked(e -> setPage(new Home(), homeBtn));

        alltheBtn = new Button("All the mods");
        alltheBtn.getStyleClass().add("sidemenu-nav-btn");
        MaterialDesignIconView<MaterialDesignIcon.T> iconAlls = new MaterialDesignIconView<>(MaterialDesignIcon.T.TURBINE);
        iconAlls.getStyleClass().add("material-design-icon-view");
        alltheBtn.setGraphic(iconAlls);
        setCanTakeAllSize(alltheBtn);
        setTop(alltheBtn);
        alltheBtn.setTranslateY(170d);
        alltheBtn.setOnMouseClicked(e -> setPage(new All(), alltheBtn));

        settingsBtn = new Button("Paramètres");
        settingsBtn.getStyleClass().add("sidemenu-nav-btn");
        MaterialDesignIconView<MaterialDesignIcon.C> iconViewss = new MaterialDesignIconView<>(MaterialDesignIcon.C.COG);
        iconViewss.getStyleClass().add("material-design-icon-view");
        settingsBtn.setGraphic(iconViewss);
        setCanTakeAllSize(settingsBtn);
        setTop(settingsBtn);
        settingsBtn.setTranslateY(210d);
        settingsBtn.setOnMouseClicked(e -> setPage(new Settings(), settingsBtn));

        sidemenu.getChildren().addAll(holyBtn,homeBtn,alltheBtn, settingsBtn);

        if (Launcher.getInstance().getAuthInfos() != null) {
            // Pseudo + avatar
            GridPane userPane = new GridPane();
            setCanTakeAllWidth(userPane);
            userPane.setMaxHeight(80);
            userPane.setMinWidth(80);
            userPane.getStyleClass().add("user-pane");
            setBottom(userPane);

            String avatarUrl = "https://minotar.net/avatar/" + (
                    saver.get("offline-username") != null ?
                            "MHF_Steve.png" :
                            Launcher.getInstance().getAuthInfos().getUuid() + ".png"
            );
            ImageView avatarView = new ImageView();
            Image avatarImg = new Image(avatarUrl);
            avatarView.setImage(avatarImg);
            avatarView.setPreserveRatio(true);
            avatarView.setFitHeight(50d);
            setCenterV(avatarView);
            setCanTakeAllSize(avatarView);
            setLeft(avatarView);
            avatarView.setTranslateX(15d);
            userPane.getChildren().add(avatarView);

            Label usernameLabel = new Label(Launcher.getInstance().getAuthInfos().getUsername());
            usernameLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
            setCanTakeAllSize(usernameLabel);
            setCenterV(usernameLabel);
            setLeft(usernameLabel);
            usernameLabel.getStyleClass().add("username-label");
            usernameLabel.setTranslateX(75d);
            setCanTakeAllWidth(usernameLabel);
            userPane.getChildren().add(usernameLabel);

            Button logoutBtn = new Button();
            final var logoutIcon = new MaterialDesignIconView<>(MaterialDesignIcon.L.LOGOUT);
            logoutIcon.getStyleClass().add("logout-icon");
            setCanTakeAllSize(logoutBtn);
            setCenterV(logoutBtn);
            setRight(logoutBtn);
            logoutBtn.getStyleClass().add("logout-btn");
            logoutBtn.setGraphic(logoutIcon);
            logoutBtn.setOnMouseClicked(e -> {
                if (currentPage instanceof Home && ((Home) currentPage).isDownloading()) {
                    return;
                }
                saver.remove("accessToken");
                saver.remove("clientToken");
                saver.remove("offline-username");
                saver.remove("msAccessToken");
                saver.remove("msRefreshToken");
                saver.save();
                Launcher.getInstance().setAuthInfos(null);
                this.panelManager.showPanel(new Login());
            });
            userPane.getChildren().add(logoutBtn);

            sidemenu.getChildren().add(userPane);
        }
    }

    @Override
    public void onShow() {
        super.onShow();
        setPage(new Home(), homeBtn);
    }



    public void setPage(ContentPanel panel, Node navButton) {

        if (currentPage instanceof Home && ((Home) currentPage).isDownloading()) {
            return;
        } else if (currentPage instanceof Holytime && ((Holytime) currentPage).isDownloading()) {
            return;
        }else if (currentPage instanceof All && ((All) currentPage).isDownloading()){
            return;
        }if (activeLink != null)
            activeLink.getStyleClass().remove("active");
        activeLink = navButton;
        activeLink.getStyleClass().add("active");

        this.navContent.getChildren().clear();
        if (panel != null) {

            this.navContent.getChildren().add(panel.getLayout());
            currentPage = panel;
            if (panel.getStylesheetPath() != null) {
                this.panelManager.getStage().getScene().getStylesheets().clear();
                this.panelManager.getStage().getScene().getStylesheets().addAll(
                        this.getStylesheetPath(),
                        panel.getStylesheetPath()
                );
            }
            panel.init(this.panelManager);
            panel.onShow();
        }
    }




    }
