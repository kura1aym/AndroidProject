package dev.androidbroadcast.quizapp.ui.edit.activity

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.androidbroadcast.quizapp.R
import dev.androidbroadcast.quizapp.domain.model.Quiz
import dev.androidbroadcast.quizapp.databinding.ActivityEditTitleBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import kotlin.properties.Delegates

class EditTitleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTitleBinding
    private lateinit var quizData: Quiz
    private var bitmap: Bitmap? = null
    private var status by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        getData()
        setColor()
        binding = ActivityEditTitleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInsets()
        setHeader()
        setSetImageButton()
        setNextButton()
        setCancelButton()
        onBackButtonPressed()
    }

    private fun getData() {
        quizData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_QUIZ, Quiz::class.java) ?: Quiz()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_QUIZ) ?: Quiz()
        }
        status = intent.getIntExtra(EXTRA_STATUS, ADD)
    }

    private fun setColor() {
        if (quizData.color != null) {
            DynamicColors.applyToActivityIfAvailable(
                this,
                DynamicColorsOptions.Builder()
                    .setContentBasedSource(quizData.color!!)
                    .build()
            )
        }
    }

    private fun getDominantColor(bitmap: Bitmap): Int {
        val newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true)
        val color = newBitmap.getPixel(0, 0)
        newBitmap.recycle()
        return color
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                setLoading(true)
                Thread {
                    val stream = contentResolver.openInputStream(it)
                    bitmap = BitmapFactory.decodeStream(stream)
                    binding.backgroundHeader.post {
                        setHeaderImg()
                        setLoading(false)
                    }
                }.start()
            }
        }

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomInset) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updatePadding(bottom = insets.bottom)

            WindowInsetsCompat.CONSUMED
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomBar) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.ime())

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = if (insets.bottom > 0) {
                    insets.bottom - binding.bottomInset.height
                } else {
                    insets.bottom
                }
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setLoading(status: Boolean) {
        if (status) {
            binding.progressIndicator.visibility = View.VISIBLE
        } else {
            binding.progressIndicator.visibility = View.GONE
        }
    }

    private fun setHeaderImg() {
        with(binding) {
            if (bitmap != null) {
                backgroundHeader.setImageBitmap(bitmap)
                backgroundHeader.alpha = 0.3F
            } else if (quizData.img != null) {
                Glide.with(this@EditTitleActivity)
                    .load(quizData.img!!.toUri())
                    .into(backgroundHeader)
                backgroundHeader.alpha = 0.3F
            }
        }
    }

    private fun setHeader() {
        with(binding) {
            backgroundHeader.layoutParams.height = (resources.displayMetrics.widthPixels / 3) * 4

            if (status == EDIT) {
                appBar.title = getString(R.string.edit_quiz)
                if (quizData.img != null) {
                    setHeaderImg()
                }
                titleEditText.setText(quizData.title)
                descEditText.setText(quizData.desc)
            }
        }
    }

    private fun setSetImageButton() {
        with(binding) {
            setImageButton.setOnClickListener {
                galleryLauncher.launch("image/*")
            }
        }
    }

    private fun setNextButton() {
        with(binding) {
            var isEmptyField: Boolean

            nextButton.setOnClickListener {
                with(quizData) {
                    title = titleEditText.text.toString().trim()
                    desc = descEditText.text.toString().trim()

                    if (title.isEmpty()) {
                        titleInput.error = "Title cannot be empty"
                    } else {
                        titleInput.error = null
                    }

                    if (desc.isEmpty()) {
                        descInput.error = "Description cannot be empty"
                    } else {
                        descInput.error = null
                    }

                    isEmptyField = title.isEmpty() || desc.isEmpty()
                }

                if (!isEmptyField) {
                    val intent = Intent(this@EditTitleActivity, EditQuestionActivity::class.java)
                    intent.putExtra(EditQuestionActivity.EXTRA_STATUS, status)

                    if (bitmap != null) {
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                val insertUri =
                                    contentResolver.insert(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        ContentValues()
                                    )

                                try {
                                    val outputStream = insertUri?.let {
                                        contentResolver.openOutputStream(it, "rw")
                                    }
                                    if (outputStream != null) {
                                        bitmap!!.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            50,
                                            outputStream
                                        )
                                    }
                                } catch (e: FileNotFoundException) {
                                    e.printStackTrace()
                                }

                                quizData.img = insertUri.toString()
                                quizData.color = getDominantColor(bitmap!!)
                            }

                            intent.putExtra(EditQuestionActivity.EXTRA_QUIZ, quizData)
                            finish()
                            startActivity(intent)
                        }
                    } else {
                        intent.putExtra(EditQuestionActivity.EXTRA_QUIZ, quizData)
                        finish()
                        startActivity(intent)
                    }
                } else {
                    MaterialAlertDialogBuilder(this@EditTitleActivity)
                        .setTitle("Empty field")
                        .setMessage("Title and description cannot be empty.")
                        .setPositiveButton("Close") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
    }

    private fun setCancelButton() {
        with(binding) {
            cancelButton.setOnClickListener {
                onBackButtonAction()
            }
        }
    }

    private fun onBackButtonPressed() {
        onBackPressedDispatcher.addCallback {
            onBackButtonAction()
        }
    }

    private fun onBackButtonAction() {
        finish()
    }

    companion object {
        const val EXTRA_QUIZ = "extra_quiz"
        const val EXTRA_STATUS = "extra_status"
        const val ADD = 0
        const val EDIT = 1
    }
}
