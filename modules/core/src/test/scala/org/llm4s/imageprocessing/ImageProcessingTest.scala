package org.llm4s.imageprocessing

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

class ImageProcessingTest extends AnyFunSuite with Matchers {

  test("analyzeImageAsync wraps sync analyzeImage in Future") {

    import java.nio.file.Files
    import java.awt.image.BufferedImage
    import java.awt.Color
    import javax.imageio.ImageIO

    val client = ImageProcessing.localImageProcessor()

    // create temp image
    val tempFile = Files.createTempFile("test", ".png")

    val image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
    val g     = image.createGraphics()
    g.setColor(Color.RED)
    g.fillRect(0, 0, 10, 10)
    g.dispose()

    ImageIO.write(image, "png", tempFile.toFile)

    try {
      val future = client.analyzeImageAsync(tempFile.toString, None)
      val result = Await.result(future, 5.seconds)

      result.isRight shouldBe true
    } finally
      Files.deleteIfExists(tempFile)
  }
}
