# Font Glyph Sheet Generator

The goal of this is to take a `ttf` file, an "alphabet" (a list of symbols), some colors and some sizes, and generate the relevant images and json info to be used with a `Text` in [indigo](https://indigoengine.io/06-presentation/text-and-fonts/).

To run:

- launch a local http server here, for example using `http-server` or IntelliJ
- run `sbt fastLinkJS`
- go to `localhost:8080` (or click on the IntelliJ button)
- click on all the download buttons you see on screen.


## Limitations

- It is hardcoded for Quicksand. But it's straightforward to change the ad hoc file.
- You need to click gazillions buttons to download everything. Making a zip file should be better.
- Colors and Sizes are hard coded.

## Note

You could also embed that directly within indigo, and generate the images and json infos at runtime.
