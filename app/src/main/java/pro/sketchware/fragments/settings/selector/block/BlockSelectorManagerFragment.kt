package pro.sketchware.fragments.settings.selector.block

import a.a.a.aB
import a.a.a.qA
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import mod.elfilibustero.sketch.lib.ui.SketchFilePickerDialog
import pro.sketchware.R
import pro.sketchware.databinding.DialogSelectorActionsBinding
import pro.sketchware.databinding.FragmentBlockSelectorManagerBinding
import pro.sketchware.fragments.settings.selector.block.details.BlockSelectorDetailsFragment
import pro.sketchware.utility.FileUtil.getExternalStorageDir
import pro.sketchware.utility.FileUtil.isExistFile
import pro.sketchware.utility.FileUtil.writeFile
import pro.sketchware.utility.SketchwareUtil.toast
import pro.sketchware.utility.SketchwareUtil.toastError
import java.io.File
import kotlin.io.readText
import pro.sketchware.databinding.DialogBlockConfigurationBinding as DialogCreateBinding

class BlockSelectorManagerFragment : qA() {

    private var _binding: FragmentBlockSelectorManagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        saved: Bundle?
    ): View {
        _binding = FragmentBlockSelectorManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var selectors: MutableList<Selector> = mutableListOf()
    private lateinit var adapter: BlockSelectorAdapter

    override fun onViewCreated(
        view: View,
        saved: Bundle?
    ) {
        configureToolbar(binding.toolbar)
        handleInsetts(binding.root)
        adapter = BlockSelectorAdapter(
            { selector, index ->
                openFragment(BlockSelectorDetailsFragment(index, selectors))
            },
            { selector, index ->
                showActionsDialog(index = index)
            }
        )
        lifecycleScope.launch {
            if (isExistFile(BlockSelectorConsts.BLOCK_SELECTORS_FILE.absolutePath)) {
                selectors = parseJson(
                    BlockSelectorConsts.BLOCK_SELECTORS_FILE.readText(
                        Charsets.UTF_8
                    )
                )
            } else {
                selectors.add(
                    Selector(
                        "typeview",
                        "Select typeview:",
                        getTypeViewList()
                    )
                )
                saveAllSelectors()
            }
        }
        binding.list.adapter = adapter
        adapter.submitList(selectors)

        binding.createNew.setOnClickListener {
            showCreateEditDialog()
        }

        super.onViewCreated(view, saved)
    }

    private fun parseJson(
        jsonString: String
    ): MutableList<Selector> {
        val gson = Gson()
        val listType = object : TypeToken<List<Selector>>() {}.type
        return gson.fromJson(jsonString, listType)
    }

    private fun showCreateEditDialog(
        index: Int = 0,
        isEdit: Boolean = false
    ) {
        val dialogBinding =
            DialogCreateBinding.inflate(LayoutInflater.from(requireContext())).apply {
                tilPalettesPath.hint = "Selector name"
                tilBlocksPath.hint = "Selector title (ex: Select View:)"
                if (isEdit) {
                    palettesPath.setText(selectors.get(index).name)
                    blocksPath.setText(selectors.get(index).title)
                }
                palettesPath.setOnTextChanged(
                    onTextChanged = {
                        if (itemAlreadyExists(it.toString())) {
                            tilPalettesPath.setError("An item with this name already exists")
                        } else {
                            tilPalettesPath.setError(null)
                        }
                    }
                )
                if (palettesPath.text?.toString().equals("typeview")) {
                    palettesPath.isEnabled = false
                    tilPalettesPath.setOnClickListener {
                        toast("You cannot change the name of this selector")
                    }
                }
            }
        val dialog = aB(requireActivity()).apply {
            dialogTitleText = if (!isEdit) "New selector" else "Edit selector"
            dialogCustomView = dialogBinding.getRoot()
            dialogYesText = if (!isEdit) "Create" else "Save"
            dialogNoText = "Cancel"
            dialogYesListener = View.OnClickListener {
                val selectorName = dialogBinding.palettesPath.text?.toString()
                val selectorTitle = dialogBinding.blocksPath.text?.toString()

                if (selectorName.isNullOrEmpty()) {
                    toast("Please type the selector's name")
                    return@OnClickListener
                }
                if (selectorTitle.isNullOrEmpty()) {
                    toast("Please type the selector's title")
                    return@OnClickListener
                }
                if (!isEdit) {
                    if (!itemAlreadyExists(selectorName)) {
                        selectors.add(
                            Selector(
                                selectorName,
                                selectorTitle,
                                mutableListOf()
                            )
                        )
                    } else {
                        toast("An item with this name already exists")
                    }
                } else {
                    selectors[index] = Selector(
                        selectorName,
                        selectorTitle,
                        selectors.get(index).data
                    )
                }
                saveAllSelectors()
                adapter.notifyDataSetChanged()
                dismiss()
            }
            dialogNoListener = View.OnClickListener {
                dismiss()
            }
        }
        dialog.show()
    }

    private fun showActionsDialog(
        index: Int
    ) {
        val dialogBinding =
            DialogSelectorActionsBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = aB(requireActivity()).apply {
            dialogTitleText = "Actions"
            dialogCustomView = dialogBinding.root
        }
        dialogBinding.apply {
            edit.setOnClickListener {
                dialog.dismiss()
                showCreateEditDialog(
                    index = index,
                    isEdit = true
                )
            }
            export.setOnClickListener {
                dialog.dismiss()
                exportSelector(
                    selector = selectors.get(index)
                )
            }
            if (selectors.get(index).name.equals("typeview")) delete.visibility = View.GONE
            delete.setOnClickListener {
                dialog.dismiss()
                showConfirmationDialog(
                    message = "Are you sure you want to delete this selector?",
                    onConfirm = {
                        selectors.removeAt(index)
                        saveAllSelectors()
                        adapter.notifyDataSetChanged()
                        it.dismiss()
                    },
                    onCancel = {
                        it.dismiss()
                    }
                )
            }
        }
        dialog.show()
    }

    private fun showConfirmationDialog(
        message: String,
        onConfirm: (aB) -> Unit,
        onCancel: (aB) -> Unit
    ) {
        val dialog = aB(requireActivity()).apply {
            dialogTitleText = "Attention"
            dialogMessageText = message
            dialogYesText = "Yes"
            dialogNoText = "Cancel"
            setCancelable(false)
            dialogYesListener = View.OnClickListener {
                onConfirm(this)
            }
            dialogNoListener = View.OnClickListener {
                onCancel(this)
            }
        }
        dialog.show()
    }

    override fun configureToolbar(toolbar: MaterialToolbar) {
        super.configureToolbar(toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.import_block_selector_menus -> {
                    showImportSelectorDialog()
                    true
                }

                R.id.export_all_block_selector_menus -> {
                    saveAllSelectors(
                        path = BlockSelectorConsts.EXPORT_FILE.absolutePath,
                        message = "Exported in ${BlockSelectorConsts.EXPORT_FILE.absolutePath}"
                    )
                    true
                }

                else -> false
            }
        }
    }

    /* not finished, for now, only only json objects are allowed,
     * like: 
     * {
     *   "data": [...],
     *   "name": "Name",
     *   "title": "Title"
     * }
     */
    private fun showImportSelectorDialog() {
        val filePickerDialog = SketchFilePickerDialog(requireActivity())
            .allowExtension("json")
            .setFilePath(getExternalStorageDir())
            .setOnFileSelectedListener { dialog, file ->
                lifecycleScope.launch {
                    handleToImportFile(file)
                }
                dialog.dismiss()
            }
        filePickerDialog.setTitle("Select .json selector file")
        filePickerDialog.a(R.drawable.file_48_blue)
        filePickerDialog.setOnDismissListener { dialog ->
            filePickerDialog.backPressed(dialog)
        }
        filePickerDialog.init()
        filePickerDialog.show()
    }

    private fun saveAllSelectors(
        path: String = BlockSelectorConsts.BLOCK_SELECTORS_FILE.absolutePath,
        message: String = "Saved"
    ) {
        writeFile(
            path,
            getGson().toJson(selectors)
        )
        toast(message)
    }

    private fun exportSelector(
        selector: Selector
    ) {
        val path = BlockSelectorConsts.EXPORT_FILE.absolutePath.replace("All_Menus", selector.name)
        writeFile(
            path,
            getGson().toJson(selector)
        )
        toast("Exported in ${path}")
    }

    private fun handleToImportFile(
        file: File
    ) {
        try {
            val json = file.readText(Charsets.UTF_8)
            if (json.isObject()) {
                val selector = getSelectorFromFile(file)
                if (selector != null) {
                    selectors.add(selector)
                    saveAllSelectors()
                    adapter.notifyDataSetChanged()
                } else {
                    toastError("Make sure you select a file that contains selector item(s).")
                }
            } else {
                val selectorsN = getSelectorsFromFile(file)
                if (selectorsN != null) {
                    selectors.addAll(selectorsN)
                    saveAllSelectors()
                    adapter.notifyDataSetChanged()
                } else {
                    toastError("Make sure you select a file that contains selector item(s).")
                }
            }
        } catch (e: Exception) {
            Log.e(BlockSelectorConsts.TAG, e.toString())
            toastError("Make sure you select a file that contains a selector item(s).")
        }
    }

    private fun getSelectorFromFile(
        path: File
    ): Selector? {
        val json = path.readText(Charsets.UTF_8)
        return try {
            getGson().fromJson(json, Selector::class.java)
        } catch (e: Exception) {
            Log.e(BlockSelectorConsts.TAG, e.toString())
            toastError("An error occurred while trying to get the selector")
            null
        }
    }

    private fun getSelectorsFromFile(
        path: File
    ): List<Selector>? {
        val json = path.readText(Charsets.UTF_8)
        val itemLstType = object : TypeToken<List<Selector>>() {}.type
        return try {
            getGson().fromJson(json, itemLstType)
        } catch (e: Exception) {
            Log.e(BlockSelectorConsts.TAG, e.toString())
            toastError("An error occurred while trying to get the selectors")
            null
        }
    }

    fun String.isObject(): Boolean {
        val jsonElement: JsonElement = JsonParser.parseString(this)
        return when {
            jsonElement.isJsonObject -> true
            jsonElement.isJsonArray -> false
            else -> false
        }
    }

    private fun getGson(): Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    private fun itemAlreadyExists(
        toCompare: String
    ): Boolean = selectors.any {
        it.name.lowercase() == toCompare.lowercase()
    }

    /*
     * A Default list of Selector Itens
     */
    private fun getTypeViewList(): List<String> {
        return listOf(
            "View",
            "ViewGroup",
            "LinearLayout",
            "RelativeLayout",
            "ScrollView",
            "HorizontalScrollView",
            "TextView",
            "EditText",
            "Button",
            "RadioButton",
            "CheckBox",
            "Switch",
            "ImageView",
            "SeekBar",
            "ListView",
            "Spinner",
            "WebView",
            "MapView",
            "ProgressBar"
        )
    }

    // big 😡
    private fun EditText.setOnTextChanged(
        onTextChanged: (CharSequence) -> Unit,
        beforeTextChanged: (CharSequence) -> Unit = { },
        afterTextChanged: () -> Unit = { }
    ) {
        this.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                s?.let {
                    onTextChanged(it)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                s?.let {
                    beforeTextChanged(it)
                }
            }

            override fun afterTextChanged(e: Editable?) {
                afterTextChanged()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}