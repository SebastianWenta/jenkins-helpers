import static java.nio.file.StandardCopyOption.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

def call (Object o){

  Path source = Paths.get(o)
  Path target = Paths.get("output")
  Files.copy(source, target, REPLACE_EXISTING)

  return "RESULT FROM CALL: " + o + "\n target"
}


