package io.github.sarcasticvlad.worktree.ui

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import io.github.sarcasticvlad.worktree.git.WorktreeSource
import java.io.File
import javax.swing.ButtonGroup
import javax.swing.JComponent

class AddWorktreeDialog(
    project: Project,
    branchNames: List<String>,
    defaultStartPoint: String?,
    defaultPath: String,
) : DialogWrapper(project) {

    private val pathField = TextFieldWithBrowseButton().apply {
        text = defaultPath
        addBrowseFolderListener(
            project,
            FileChooserDescriptorFactory.createSingleFolderDescriptor()
                .withTitle("Select Worktree Location")
                .withDescription("Directory for the new worktree"),
        )
    }

    private val existingRadio = JBRadioButton("From existing branch", true)
    private val newRadio = JBRadioButton("Create new branch")

    private val existingBranchCombo = ComboBox(branchNames.toTypedArray()).apply {
        if (defaultStartPoint != null) selectedItem = defaultStartPoint
    }
    private val newBranchNameField = JBTextField()
    private val newBranchStartCombo = ComboBox(branchNames.toTypedArray()).apply {
        if (defaultStartPoint != null) selectedItem = defaultStartPoint
    }

    init {
        ButtonGroup().apply { add(existingRadio); add(newRadio) }
        existingRadio.addChangeListener { updateEnabled() }
        title = "Add Worktree"
        init()
        updateEnabled()
    }

    private fun updateEnabled() {
        val existing = existingRadio.isSelected
        existingBranchCombo.isEnabled = existing
        newBranchNameField.isEnabled = !existing
        newBranchStartCombo.isEnabled = !existing
    }

    override fun createCenterPanel(): JComponent =
        FormBuilder.createFormBuilder()
            .addLabeledComponent("Location:", pathField)
            .addComponent(existingRadio)
            .addLabeledComponent("Branch:", existingBranchCombo)
            .addComponent(newRadio)
            .addLabeledComponent("New branch name:", newBranchNameField)
            .addLabeledComponent("Start point:", newBranchStartCombo)
            .panel

    override fun doValidate(): ValidationInfo? {
        val path = pathField.text.trim()
        if (path.isEmpty()) return ValidationInfo("Specify a location", pathField)
        val dir = File(path)
        if (dir.exists() && dir.list()?.isNotEmpty() == true) {
            return ValidationInfo("Directory already exists and is not empty", pathField)
        }
        if (existingRadio.isSelected) {
            if (existingBranchCombo.selectedItem == null) {
                return ValidationInfo("Select a branch", existingBranchCombo)
            }
        } else {
            if (newBranchNameField.text.isBlank()) {
                return ValidationInfo("Enter new branch name", newBranchNameField)
            }
            if (newBranchStartCombo.selectedItem == null) {
                return ValidationInfo("Select a start point", newBranchStartCombo)
            }
        }
        return null
    }

    fun selectedPath(): String = pathField.text.trim()

    fun selectedSource(): WorktreeSource =
        if (existingRadio.isSelected) {
            WorktreeSource.ExistingBranch(existingBranchCombo.selectedItem as String)
        } else {
            WorktreeSource.NewBranch(
                newBranchNameField.text.trim(),
                newBranchStartCombo.selectedItem as String,
            )
        }
}
