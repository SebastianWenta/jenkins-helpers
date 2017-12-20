import static java.nio.file.StandardCopyOption.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


def call (Object sourcePath, String targetPath){

  Path source = Paths.get(sourcePath.toString())
  Path target
  if (System.properties['os.name'].toLowerCase().contains('windows')){
    target = Paths.get(targetPath + "\\target.xlsx")
  } else {
    target = Paths.get(targetPath + "/target.xlsx")
  }

  return Files.copy(source, target, REPLACE_EXISTING).toAbsolutePath().toString()


}

