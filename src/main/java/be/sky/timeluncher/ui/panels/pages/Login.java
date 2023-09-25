package be.sky.timeluncher.ui.panels.pages;

import be.sky.timeluncher.Launcher;
import be.sky.timeluncher.ui.PanelManager;
import be.sky.timeluncher.ui.panel.Panel;
import fr.litarvan.openauth.AuthPoints;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.model.AuthAgent;
import fr.litarvan.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;


public class Login extends Panel {
    public static GridPane loginCard = new GridPane();
    private GridPane bgImage;
    private Timeline imageChangeTimeline; // Déclarez la variable de classe ici
    Saver saver = Launcher.getInstance().getSaver();
    AtomicBoolean offlineAuth = new AtomicBoolean(false);

    TextField userField = new TextField();
    PasswordField passwordField = new PasswordField();
    Label userErrorLabel = new Label();
    Label passwordErrorLabel = new Label();
    Button btnLogin = new Button("Connexion");
    CheckBox authModeChk = new CheckBox("Mode crack");
    Button msLoginBtn = new Button();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/login.css";
    }

    public Login() {
        // Initialisez vos autres variables si nécessaire
        // ...

        // Créez une liste d'URLs d'images que vous souhaitez utiliser en arrière-plan
        List<String> imageUrls = Arrays.asList(
                "/images/pixelmon.png",
                "/images/holy.png",
                "/images/allthemods.png"
        );

        AtomicInteger currentImageIndex = new AtomicInteger();

        bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 1, 0);

        // Utilisation de la classe Image pour précharger la première image
        Image firstImage = new Image(getClass().getResourceAsStream(imageUrls.get(currentImageIndex.get())));
        bgImage.setStyle("-fx-background-image: url('" + imageUrls.get(currentImageIndex.get()) + "');");

        currentImageIndex.getAndIncrement();

        imageChangeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> {
                    // Changez l'image de fond vers la suivante
                    if (currentImageIndex.get() >= imageUrls.size()) {
                        currentImageIndex.set(0); // Revenez à la première image si nous sommes arrivés à la dernière
                    }

                    // Utilisation de la classe Image pour précharger l'image suivante
                    Image nextImage = new Image(getClass().getResourceAsStream(imageUrls.get(currentImageIndex.get())));

                    // Créez un nouvel élément GridPane pour la transition
                    GridPane transitionPane = new GridPane();
                    setCanTakeAllSize(transitionPane);
                    transitionPane.getStyleClass().add("bg-image");
                    transitionPane.setStyle("-fx-background-image: url('" + imageUrls.get(currentImageIndex.get()) + "');");

                    // Ajoutez le nouvel élément GridPane pour la transition
                    this.layout.add(transitionPane, 1, 0);

                    // Définissez l'opacité initiale à 0
                    transitionPane.setOpacity(0);

                    // Animation de fondu
                    FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), transitionPane);
                    fadeTransition.setFromValue(0);
                    fadeTransition.setToValue(1);
                    fadeTransition.setOnFinished(e -> {
                        // Supprimez l'ancien élément GridPane de l'arrière-plan
                        this.layout.getChildren().remove(bgImage);

                        // Remplacez l'image de fond actuelle par la nouvelle
                        bgImage = transitionPane;
                        currentImageIndex.getAndIncrement();
                        imageChangeTimeline.playFromStart();
                    });
                    fadeTransition.play();
                })
        );
        imageChangeTimeline.setCycleCount(Timeline.INDEFINITE);
        imageChangeTimeline.play();

        // ... Le reste de votre code
    }


    @Override
    public void init(PanelManager panelManager) {

        super.init(panelManager);


                // Background
        this.layout.getStyleClass().add("login-layout");

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(350);
        columnConstraints.setMaxWidth(350);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());
        this.layout.add(loginCard, 0, 0);





        // Login card
        setCanTakeAllSize(this.layout);
        loginCard.getStyleClass().add("login-card");
        loginCard.setStyle("-fx-background-image: url('/images/stars.png');");
        setLeft(loginCard);
        setCenterH(loginCard);
        setCenterV(loginCard);
        /*
         * Login sidebar
         */
        Label title = new Label("Time Launcher");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 30f));
        title.getStyleClass().add("login-title");
        setCenterH(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setTranslateY(30d);
        loginCard.getChildren().add(title);

        // Username/E-Mail
        setCanTakeAllSize(userField);
        setCenterV(userField);
        setCenterH(userField);
        userField.setPromptText("Adresse E-Mail");
        userField.setMaxWidth(300);
        userField.setTranslateY(-70d);
        userField.getStyleClass().add("login-input");
        userField.textProperty().addListener((_a, oldValue, newValue) -> this.updateLoginBtnState(userField, userErrorLabel));

        // User error
        setCanTakeAllSize(userErrorLabel);
        setCenterV(userErrorLabel);
        setCenterH(userErrorLabel);
        userErrorLabel.getStyleClass().add("login-error");
        userErrorLabel.setTranslateY(-45d);
        userErrorLabel.setMaxWidth(280);
        userErrorLabel.setTextAlignment(TextAlignment.LEFT);

        // Password
        setCanTakeAllSize(passwordField);
        setCenterV(passwordField);
        setCenterH(passwordField);
        passwordField.setPromptText("Mot de passe");
        passwordField.setMaxWidth(300);
        passwordField.setTranslateY(-15d);
        passwordField.getStyleClass().add("login-input");
        passwordField.textProperty().addListener((_a, oldValue, newValue) -> this.updateLoginBtnState(passwordField, passwordErrorLabel));

        // User error
        setCanTakeAllSize(passwordErrorLabel);
        setCenterV(passwordErrorLabel);
        setCenterH(passwordErrorLabel);
        passwordErrorLabel.getStyleClass().add("login-error");
        passwordErrorLabel.setTranslateY(10d);
        passwordErrorLabel.setMaxWidth(280);
        passwordErrorLabel.setTextAlignment(TextAlignment.LEFT);

        // Login button
        setCanTakeAllSize(btnLogin);
        setCenterV(btnLogin);
        setCenterH(btnLogin);
        btnLogin.setDisable(true);
        btnLogin.setMaxWidth(300);
        btnLogin.setTranslateY(40d);
        btnLogin.getStyleClass().add("login-log-btn");
        btnLogin.setOnMouseClicked(e -> this.authenticate(userField.getText(), passwordField.getText()));

        setCanTakeAllSize(authModeChk);
        setCenterV(authModeChk);
        setCenterH(authModeChk);
        authModeChk.getStyleClass().add("login-mode-chk");
        authModeChk.setMaxWidth(300);
        authModeChk.setTranslateY(85d);
        authModeChk.selectedProperty().addListener((e, old, newValue) -> {
            offlineAuth.set(newValue);
            passwordField.setDisable(newValue);
            if (newValue) {
                userField.setPromptText("Pseudo");
                passwordField.clear();
            } else {
                userField.setPromptText("Adresse E-Mail");
            }

            btnLogin.setDisable(!(userField.getText().length() > 0 && (offlineAuth.get() || passwordField.getText().length() > 0)));
        });

        Separator separator = new Separator();
        setCanTakeAllSize(separator);
        setCenterH(separator);
        setCenterV(separator);
        separator.getStyleClass().add("login-separator");
        separator.setMaxWidth(300);
        separator.setTranslateY(110d);

        // Login with label
        Label loginWithLabel = new Label("Ou se connecter avec:".toUpperCase());
        setCanTakeAllSize(loginWithLabel);
        setCenterV(loginWithLabel);
        setCenterH(loginWithLabel);
        loginWithLabel.setFont(Font.font(loginWithLabel.getFont().getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 14d));
        loginWithLabel.getStyleClass().add("login-with-label");
        loginWithLabel.setTranslateY(130d);
        loginWithLabel.setMaxWidth(280d);

        // Microsoft login button
        ImageView view = new ImageView(new Image("images/microsoft.png"));
        view.setPreserveRatio(true);
        view.setFitHeight(30d);
        setCanTakeAllSize(msLoginBtn);
        setCenterH(msLoginBtn);
        setCenterV(msLoginBtn);
        msLoginBtn.getStyleClass().add("ms-login-btn");
        msLoginBtn.setMaxWidth(300);
        msLoginBtn.setTranslateY(165d);
        msLoginBtn.setGraphic(view);
        msLoginBtn.setOnMouseClicked(e -> this.authenticateMS());

        loginCard.getChildren().addAll(userField, userErrorLabel, passwordField, passwordErrorLabel, authModeChk, btnLogin, separator, loginWithLabel, msLoginBtn);
    }

    public void updateLoginBtnState(TextField textField, Label errorLabel) {
        if (offlineAuth.get() && textField == passwordField) return;

        if (textField.getText().length() == 0) {
            errorLabel.setText("Le champ ne peut être vide");
        } else {
            errorLabel.setText("");
        }

        btnLogin.setDisable(!(userField.getText().length() > 0 && (offlineAuth.get() || passwordField.getText().length() > 0)));
    }

    public void authenticate(String user, String password) {
        if (offlineAuth.get()) {
            String storedUsername = "";
            String storedPassword = "";

            try {
                // Télécharger le fichier JSON depuis l'URL
                URL url = new URL("https://votre-hebergeur.com/chemin/vers/credentials.json");
                InputStream inputStream = url.openStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                // Lire le fichier JSON et extraire les données de connexion
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(reader);
                storedUsername = (String) jsonObject.get("username");
                storedPassword = (String) jsonObject.get("password");
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

            if (!storedUsername.isEmpty() && user.equals(storedUsername) && password.equals(storedPassword)) {
                // Authentification réussie en mode "crack"

                AuthInfos infos = new AuthInfos(
                        user,
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString()
                );

                saver.set("offline-username", infos.getUsername());
                saver.save();
                Launcher.getInstance().setAuthInfos(infos);

                this.logger.info("Hello " + infos.getUsername());

                panelManager.showPanel(new App());
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Authentification échouée");
                    alert.setContentText("Nom d'utilisateur ou mot de passe incorrect.");
                    alert.showAndWait();
                });
            }

        } else {
            Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);

            try {
                AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, user, password, null);

                saver.set("accessToken", response.getAccessToken());
                saver.set("clientToken", response.getClientToken());
                saver.save();

                AuthInfos infos = new AuthInfos(
                        response.getSelectedProfile().getName(),
                        response.getAccessToken(),
                        response.getClientToken(),
                        response.getSelectedProfile().getId()
                );

                Launcher.getInstance().setAuthInfos(infos);

                this.logger.info("Hello " + infos.getUsername());

                panelManager.showPanel(new App());
            } catch (AuthenticationException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Une erreur est survenue lors de la connexion.");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
            }
        }
    }
    public void authenticateMS() {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        authenticator.loginWithAsyncWebview().whenComplete((response, error) -> {
            if (error != null) {
                Launcher.getInstance().getLogger().err(error.toString());
                Platform.runLater(()-> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setContentText(error.getMessage());
                    alert.show();
                });

                return;
            }

            saver.set("msAccessToken", response.getAccessToken());
            saver.set("msRefreshToken", response.getRefreshToken());
            saver.save();
            Launcher.getInstance().setAuthInfos(new AuthInfos(
                    response.getProfile().getName(),
                    response.getAccessToken(),
                    response.getProfile().getId(),
                    response.getXuid(),
                    response.getClientId()
            ));

            Launcher.getInstance().getLogger().info("Hello " + response.getProfile().getName());

            Platform.runLater(() -> panelManager.showPanel(new App()));
        });
    }
}