import static java.nio.file.StandardCopyOption.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

def call (String s, String t){
  Path source = Paths.get(s)
  Path target = Paths.get(t)
  Files.copy(source, target, REPLACE_EXISTING)
}


