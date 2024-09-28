package entrypoint

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import scala.concurrent.ExecutionContext.Implicits.global
import io.circe.Encoder
import glyphgenerator.GlyphInfo
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.Success
import scala.util.Failure

@main def run(): Unit = {
  val fontFace =
    glyphgenerator.FontFace("Quicksand", "url('Quicksand-Bold.ttf') format('truetype')")

  val letters      = ('a'.toByte.toInt to 'z'.toByte.toInt).map(_.toByte.toChar).toVector
  val upperLetters = letters.map(_.toUpper)
  val digits       = (0 to 9).map(_.toString.head).toVector
  val symbols      = Vector('.', '?', '!', ' ', '#', '>', '<', ':', '/', '€')

  val alphabet = Vector(
    letters,
    upperLetters,
    digits,
    symbols
  ).flatten

  val sizes  = Vector(8, 12, 16, 20)
  val colors = Vector("white", "black", "green", "red")

  def download(size: Int, color: String): Unit = {
    def textContent(text: String) =
      "data:text/plaincharset=utf-8," + js.URIUtils.encodeURIComponent(text)
    def downloadFile(filename: String, content: String): Unit = {
      val element = dom.document.createElement("a").asInstanceOf[dom.HTMLAnchorElement]
      element.setAttribute(
        "href",
        content
      )
      element.setAttribute("download", filename)

      element.style.display = "none"
      dom.document.body.appendChild(element)

      element.click()

      dom.document.body.removeChild(element)
    }

    glyphgenerator
      .createGlyphFontData(fontFace, size, color, alphabet)
      .map { (imageData, _, glyphInfo) =>
        val file = s"${fontFace.family}-${glyphInfo.color}-${glyphInfo.fontSize}"
        println(s"Downloading image $file")
        val glyphInfoJsonString = Encoder[GlyphInfo].apply(glyphInfo).noSpaces
        downloadFile(
          s"$file.png",
          imageData
        )
        downloadFile(s"$file.json", textContent(glyphInfoJsonString))

      }
      .onComplete {
        case Success(_)         => ()
        case Failure(exception) => throw exception
      }
  }

  render(
    dom.document.getElementById("root"),
    div(
      h1("Glyph generator!"),
      for {
        size  <- sizes
        color <- colors
      } yield button(
        s"Download $color-$size",
        onClick.mapToUnit --> Observer[Any] { _ =>
          download(size, color)
        }
      ),
      child <-- EventStream
        .fromFuture(
          glyphgenerator
            .createGlyphFontData(
              fontFace,
              32,
              "red",
              alphabet
            )
        )
        .map { (imgData, _, glyphInfo) =>
          div(
            img(src := imgData),
            pre(
              Encoder[GlyphInfo].apply(glyphInfo).spaces2
            )
          )
        }
    )
  )

}
