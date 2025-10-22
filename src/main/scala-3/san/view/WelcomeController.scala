package san.view

import javafx.fxml.FXML
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import san.service.Router

class WelcomeController {

  @FXML private var mediaView: MediaView = _ // Component for displaying video
  private var player: MediaPlayer = _ // Media player instance

  @FXML private def initialize(): Unit = {
    val url = getClass.getResource("/videos/welcome_background.mp4")
    if (url != null) {
      val media = new Media(url.toExternalForm)
      player = new MediaPlayer(media)
      player.setCycleCount(MediaPlayer.INDEFINITE)  // loop
      player.setAutoPlay(true)
      mediaView.setMediaPlayer(player)

      // Size video to fill the center area
      mediaView.sceneProperty.addListener((_,_,scene) =>
        if (scene != null) {
          mediaView.fitWidthProperty.bind(scene.widthProperty)
          mediaView.fitHeightProperty.bind(scene.heightProperty)
        }
      )
    }
  }

  // Begin main application experience when user clicks Start
  @FXML def start(): Unit = {
    if (player != null) {
      try player.stop() finally player.dispose()  // release file handle (Windows)
    }
    // Navigate to login page
    Router.showCenter("/san/view/Login.fxml")
  }
}
