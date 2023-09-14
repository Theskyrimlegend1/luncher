package be.sky.timeluncher;

import be.sky.timeluncher.ui.PanelManager;
import be.sky.timeluncher.ui.panels.pages.App;
import be.sky.timeluncher.ui.panels.pages.Login;

import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.litarvan.openauth.AuthPoints;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.litarvan.openauth.model.AuthProfile;
import fr.litarvan.openauth.model.response.RefreshResponse;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class Launcher extends Application {

    private static Launcher instance;

    private static Launcher instancepix;

    private final ILogger pixlogger;
    private final ILogger logger;
    private final Path launcherDir = GameDirGenerator.createGameDir("launcher-fx\\Holytime", true);
    //pixelmon
    private final Path luncherDir = GameDirGenerator.createGameDir("launcher-fx\\pixelmon", true);
    private final Saver saver;

    private final Saver pixsaver;
    private PanelManager panelManager;
    private AuthInfos authInfos = null;

    public Launcher() {
        instance = this;
        this.logger = new Logger("[LauncherFX]", this.launcherDir.resolve("launcher.log"));
        if (Files.notExists(this.launcherDir))
        {
            try
            {
                Files.createDirectory(this.launcherDir);
            } catch (IOException e)
            {
                this.logger.err("Unable to create launcher folder");
                this.logger.printStackTrace(e);
            }
        }

        saver = new Saver(this.launcherDir.resolve("config.properties"));
        saver.load();


        instancepix = this;
        this.pixlogger = new Logger("[LauncherPIX]", this.luncherDir.resolve("launcher.log"));
        if (Files.notExists(this.luncherDir))
        {
            try
            {
                Files.createDirectory(this.luncherDir);
            } catch (IOException e)
            {
                this.pixlogger.err("Unable to create launcher folder");
                this.pixlogger.printStackTrace(e);
            }
        }

        pixsaver = new Saver(this.launcherDir.resolve("config.properties"));
        pixsaver.load();
    }

    public static Launcher getInstance() {
        return instance;
    }

    public static Launcher getInstancePix(){return instancepix;}



    @Override
    public void start(Stage stage) {
        this.logger.info("Starting launcher");
        this.panelManager = new PanelManager(this, stage);
        this.panelManager.init();

        if (this.isUserAlreadyLoggedIn()) {
            logger.info("Hello " + authInfos.getUsername());

            this.panelManager.showPanel(new App());
        } else {
            this.panelManager.showPanel(new Login());
        }
    }

    public boolean isUserAlreadyLoggedIn() {
        if (saver.get("accessToken") != null && saver.get("clientToken") != null) {
            Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);

            try {
                RefreshResponse response = authenticator.refresh(saver.get("accessToken"), saver.get("clientToken"));
                saver.set("accessToken", response.getAccessToken());
                saver.set("clientToken", response.getClientToken());
                saver.save();
                this.setAuthInfos(new AuthInfos(
                        response.getSelectedProfile().getName(),
                        response.getAccessToken(),
                        response.getClientToken(),
                        response.getSelectedProfile().getId()
                ));

                return true;
            } catch (AuthenticationException ignored) {
                saver.remove("accessToken");
                saver.remove("clientToken");
                saver.save();
            }
        } else if (saver.get("msAccessToken") != null && saver.get("msRefreshToken") != null) {
            try {
                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                MicrosoftAuthResult response = authenticator.loginWithRefreshToken(saver.get("msRefreshToken"));

                saver.set("msAccessToken", response.getAccessToken());
                saver.set("msRefreshToken", response.getRefreshToken());
                saver.save();
                this.setAuthInfos(new AuthInfos(
                        response.getProfile().getName(),
                        response.getAccessToken(),
                        response.getProfile().getId(),
                        response.getXuid(),
                        response.getClientId()
                ));
                return true;
            } catch (MicrosoftAuthenticationException e) {
                saver.remove("msAccessToken");
                saver.remove("msRefreshToken");
                saver.save();
            }
        } else if (saver.get("offline-username") != null) {
            this.authInfos = new AuthInfos(saver.get("offline-username"), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            return true;
        }

        return false;
    }

    public AuthInfos getAuthInfos() {
        return authInfos;
    }

    public void setAuthInfos(AuthInfos authInfos) {
        this.authInfos = authInfos;
    }

    public ILogger getLogger() {
        return logger;
    }

    public Saver getSaver() {
        return saver;
    }

    public Path getLauncherDir() {
        return launcherDir;
    }

    public Path getLuncherDir(){
        return luncherDir;
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public void hideWindow() {
        this.panelManager.getStage().hide();
    }
}
