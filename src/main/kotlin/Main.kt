import javafx.application.Application
import javafx.application.Platform
import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.stage.Stage
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*


val testrepo = File("${System.getProperty("user.dir")}/test/")
var currentdirectory = testrepo
var list = ListView<String>()
var l: MutableList<String> = currentdirectory.list().toMutableList()
var filedirectory: MutableList<File> = currentdirectory.listFiles().toMutableList()
var statusline = Label()

class Main : Application() {
    fun renamingFunction() {
        // text input
        val original = filedirectory[list.selectionModel.selectedIndex]
        val newname: File
        val dialog = TextInputDialog("")
        dialog.title = "File renaming"
        dialog.headerText = "Enter new name."
        val result = dialog.showAndWait()
        if (result.isPresent) {
            if (ButtonType.OK.buttonData == ButtonBar.ButtonData.OK_DONE) {
                val temp1 = original.toString().lastIndexOf(File.separator)
                newname = File(original.toString().substring(0, temp1))
                currentdirectory = File (newname.toString() + File.separator + result.get())

                val flag: Boolean = original.renameTo(currentdirectory)
                if (flag) {
                    list.fireEvent(ListView.EditEvent(list, ListView.editCommitEvent(), result.get(),
                        list.selectionModel.selectedIndex))
                    l[list.selectionModel.selectedIndex] = result.get()
                    filedirectory[list.selectionModel.selectedIndex] = currentdirectory
                } else {
                    val alert = Alert(Alert.AlertType.ERROR)
                    alert.title = "Warning"
                    alert.contentText = "This is invalid."
                    alert.showAndWait()
                }
            }
        }
    }

    fun hiddenfilefinder(){
//        for(h in l) {
//            if (h.startsWith(".")) {
//                list.items.remove(h)
//            }
//        }
    }

    fun showfile(){
        list.items.clear()
        l = currentdirectory.list().toMutableList()
        for(h in l) {
            list.items.add(h)
        }
    }

    fun deletefiles() {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.title = "Warning"
        alert.contentText = "Delete file permanently?"
        val nextstep = alert.showAndWait()
        if (nextstep.isPresent) {
            when (nextstep.get()) {
                ButtonType.OK -> {
                    l.removeAt(list.selectionModel.selectedIndex)
                    filedirectory[list.selectionModel.selectedIndex].delete()
                    list.items.removeAt(list.selectionModel.selectedIndex)
                }
            }
            l = currentdirectory.list().toMutableList()
            filedirectory = currentdirectory.listFiles().toMutableList()
        }
    }

    fun next(){
        currentdirectory = filedirectory[list.selectionModel.selectedIndex]
        if (currentdirectory.isDirectory) {
            list.items.clear()
            l = currentdirectory.list().toMutableList()
            filedirectory = currentdirectory.listFiles().toMutableList()
            for (h in l) {
                list.items.add(h)
            }
        }
    }


    fun prev(){
        var temp = currentdirectory.toString().lastIndexOf(File.separator)
        currentdirectory = File(currentdirectory.toString().substring(0,temp))
        if (currentdirectory.isDirectory) {
            list.items.clear()
            l = currentdirectory.list().toMutableList()
            filedirectory = currentdirectory.listFiles().toMutableList()
            for (h in l) {
                list.items.add(h)
            }
        }
    }

    fun moveFunction(){
        val sourcepath =  filedirectory[list.selectionModel.selectedIndex].toPath()
        val dialog = TextInputDialog("")
        dialog.title = "File Moving"
        dialog.headerText = "Enter new location."
        val destination = dialog.showAndWait()
        if (destination.isPresent) {
            Files.move(sourcepath, File(destination.get()).toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        //Files.move(sourcepath, File(destination.get().toString()), StandardCopyOption.REPLACE_EXISTING);
    }
    override fun start(stage: Stage) {
        list.items.clear()
        currentdirectory = testrepo
        l = currentdirectory.list().toMutableList()
        filedirectory = currentdirectory.listFiles().toMutableList()
        for (h in l) {
            list.items.add(h)
        }

        // CREATE WIDGETS TO DISPLAY
        // menubar & toolbar
        val menuBar = MenuBar()
        val fileMenu = Menu("File")
        val viewMenu = Menu("View")
        val actionsMenu = Menu("Actions")
        val optionsMenu = Menu("Options")
        val prevMenu = MenuItem("Prev")
        val nextMenu = MenuItem("Next")
        prevMenu.setOnAction { prev() }
        nextMenu.setOnAction { next() }

        val menuNew = MenuItem("New")
        val menuOpen = MenuItem("Open")
        val menuClose = MenuItem("Close")
        val menuQuit = MenuItem("Quit")
        menuQuit.setOnAction { Platform.exit() }

        val menuRename = MenuItem("Rename")
        val menuMove = MenuItem("Move")
        val menuDelete = MenuItem("Delete")
        menuRename.setOnAction { renamingFunction()}
        menuDelete.setOnAction { deletefiles()}

        val hiddenToggle = RadioMenuItem("Show Hidden Files")
        hiddenToggle.setOnAction {
            if(hiddenToggle.selectedProperty().get()){
                hiddenfilefinder()
            }else{
                showfile()
            }
        }

        fileMenu.items.addAll(menuNew, menuOpen, menuClose, menuQuit)
        viewMenu.items.addAll(prevMenu,nextMenu)
        actionsMenu.items.addAll(menuRename, menuMove, menuDelete)
        optionsMenu.items.addAll(hiddenToggle)
        menuBar.menus.addAll(fileMenu, viewMenu,actionsMenu, optionsMenu )

        val toolbar = ToolBar()
        // creating the home button
        val iv1 = ImageView(Image("home.png"))
        iv1.fitWidth = 20.0
        iv1.fitHeight = 20.0
        val homebutton = Button("Home",iv1)

        // creating the prev button
        val iv2 = ImageView(Image("back.png"))
        iv2.fitWidth = 20.0
        iv2.fitHeight = 20.0
        val prevbutton = Button("Prev",iv2)

        // creating the next button
        val iv3 = ImageView(Image("next.png"))
        iv3.fitWidth = 20.0
        iv3.fitHeight = 20.0
        val nextbutton = Button("Next", iv3)

        // creating the delete button
        val iv4 = ImageView(Image("bin.png"))
        iv4.fitWidth = 20.0
        iv4.fitHeight = 20.0
        val deletebutton = Button("Delete", iv4)

        // creating the rename button
        val iv5 = ImageView(Image("rename.png"))
        iv5.fitWidth = 20.0
        iv5.fitHeight = 20.0
        val renamebutton = Button("Rename",iv5)

        // creating the rename button
        val iv6 = ImageView(Image("move.png"))
        iv6.fitWidth = 20.0
        iv6.fitHeight = 20.0
        val movebutton = Button("Move",iv6)
        toolbar.items.addAll(homebutton, prevbutton,nextbutton, deletebutton, renamebutton, movebutton)

        // stack menu and toolbar in the top region
        val vbox = VBox(menuBar, toolbar)

        // Displaying the list view when pressing the home button
        val eventHandler = EventHandler { _: MouseEvent ->
                list.items.clear()
                currentdirectory = testrepo
                l = currentdirectory.list().toMutableList()
                filedirectory = currentdirectory.listFiles().toMutableList()
                for (h in l) {
                    list.items.add(h)
                }
            }
        homebutton.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler)

        //Going backward in the directory when pressing the prev button
        val eventHandler2 = EventHandler { _: Event -> prev() }
        prevbutton.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler2)

        //Going forward in the directory when pressing the next button
        val eventHandler3 = EventHandler { _: Event -> next() }
        nextbutton.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler3)

        list.onMouseClicked = EventHandler<MouseEvent> { click ->
            if (click.clickCount == 2) {
                next()
            }
        }

        //Renaming a file when pressing the rename button
        val eventHandler4 = EventHandler { _: Event -> renamingFunction() }
        renamebutton.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler4)

        // Moves a file to a different directory when pressing the move button
        val eventHandler5 = EventHandler { _: Event -> moveFunction() }
        movebutton.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler5)

        //deletes a file/directory when pressing the delete button
        val eventHandler6 = EventHandler { _: Event -> deletefiles() }
        deletebutton.addEventFilter(MouseEvent.MOUSE_CLICKED, eventHandler6)

        // Making the statusline
        list.selectionModel.selectIndices(0)
        list.selectionModel.selectedItemProperty().addListener { _, _, _ ->
            if(list.selectionModel.selectedIndex >= 0) {
                statusline.text = filedirectory[list.selectionModel.selectedIndex].toString()
            }
        }

        list.onKeyPressed = EventHandler { k ->
            if(k.code.equals(KeyCode.BACK_SPACE)|| k.code.equals(KeyCode.DELETE)){
                prev()
            }else if (k.code.equals(KeyCode.ENTER)) {
                next()
            }
        }

        // SETUP LAYOUT
        val blank = VBox()
        val v = TextArea()
        v.font = Font(20.0)

        val border = BorderPane()
        border.top = vbox
        border.left = list
        border.bottom = statusline

        list.selectionModel.selectedItemProperty().addListener { _, _, _ ->
            if(!list.selectionModel.selectedItem.isNullOrEmpty()) {
                if(filedirectory[list.selectionModel.selectedIndex].isDirectory) {
                    border.center = blank
                }else if (list.selectionModel.selectedItem.endsWith(".png")
                    || list.selectionModel.selectedItem.endsWith(".jpg")
                    || list.selectionModel.selectedItem.endsWith(".bmp")){
                    val img = ImageView(Image(filedirectory[list.selectionModel.selectedIndex].toString()))
                    img.fitWidth = border.minWidth
                    img.fitHeight = border.minHeight
                    border.center = img
                } else if(list.selectionModel.selectedItem.endsWith(".txt")
                    || list.selectionModel.selectedItem.endsWith(".md")) {
                    val inputtext = Scanner(FileReader(filedirectory[list.selectionModel.selectedIndex]))
                    var content = String()
                    while (inputtext.hasNextLine()) {
                        content += inputtext.nextLine()
                    }
                    v.text = content
                    border.center = v
                }else if(!Files.isReadable(filedirectory[list.selectionModel.selectedIndex].toPath())) {
                    v.text = "File cannot be read"
                    border.center = v
                }else
                 {
                     v.text = "Unsupported type"
                    border.center = v
                }
            }
        }

        // CREATE AND SHOW SCENE
        val scene = Scene(border, 800.0, 600.0)
        stage.scene = scene
        stage.title = "File browser"
        stage.show()
    }
}
