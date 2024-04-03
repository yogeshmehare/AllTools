package com.yogesh.alltools.imagetopdf

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.yogesh.alltools.BuildConfig
import com.yogesh.alltools.R
import com.yogesh.alltools.databinding.FragmentImageSelectionBinding
import com.yogesh.alltools.imagetopdf.PictureContent.loadSavedImages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ImagesSelectionFragment : Fragment() {

    private var selectMoreImagesClicked: Boolean = false
    private val TAG: String? = this.tag
    private lateinit var permissionDialog: Dialog
    private var _binding: FragmentImageSelectionBinding? = null
    private val binding get() = _binding!!
    private var photoFile: File? = null
    val CAPTURE_IMAGE_REQUEST = 1
    val CAPTURE_IMAGE_FROM_GALLERY_REQUEST = 2
    var mCurrentPhotoPath: String = ""
    var imageExtension: String = ".JPG"

    companion object{
        fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
            val ei = ExifInterface(
                selectedImage.path!!
            )
            val orientation =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
                else -> img
            }
        }

        private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
            img.recycle()
            return rotatedImg
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageSelectionBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        imageItems.add(Image(path = "https://picsum.photos/600/400"))
//        imageItems.add(Image(path = "https://picsum.photos/700"))
//        imageItems.add(Image(path = "https://picsum.photos/400"))
//        imageItems.add(Image(path = "https://picsum.photos/800/300"))
//        imageItems.add(Image(path = "https://picsum.photos/200/400"))
//        imageItems.add(Image(path = "https://picsum.photos/500"))
//        imageItems.add(Image(path = "https://picsum.photos/500/200"))
//        imageItems.add(Image(path = "https://picsum.photos/200/400"))


        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.image_to_pdf_menu, menu)

                val item = menu.findItem(R.id.selectAll)
                if(item!=null) {
                    val iv = item.actionView!!.findViewById<View>(R.id.selectAllCheckBox) as CheckBox
                    iv.setOnCheckedChangeListener { buttonView, isChecked ->
                        if(isChecked) {
                            for (picItem in PictureContent.items) {
                                picItem.checked = true
                            }
                            binding.rv.adapter?.notifyDataSetChanged()
                        }else{
                            for (picItem in PictureContent.items) {
                                picItem.checked = false
                            }
                            binding.rv.adapter?.notifyDataSetChanged()
                        }
                    }
                }

                val convertToPdfItem = menu.findItem(R.id.covert_to_pdf)
                if(convertToPdfItem!=null) {
                    val iv = convertToPdfItem.actionView!!.findViewById<View>(R.id.convertToPdfButton) as MaterialCardView
                    iv.setOnClickListener {
                        convertToPdf()
                    }
                }
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.delete_icon -> {
                        Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
                        val vItem = requireActivity().findViewById<View>(R.id.delete_icon)
                        val popMenu = PopupMenu(requireActivity(), vItem)
                        popMenu.menu.add(R.string.delete_selected).setOnMenuItemClickListener {
                            val list = mutableListOf<PictureItem>()
                            for (item in PictureContent.items){
                                if(item.checked){
                                    list.add(item)
                                    deleteFile(item.uri!!)
                                }
                            }
                            if(list.size==0){
                                Toast.makeText(requireContext(), "No Item Selected", Toast.LENGTH_SHORT).show()
                            }
                            PictureContent.items.removeAll(list.toSet())
                            binding.rv.adapter?.notifyDataSetChanged()

                            true
                        }
                        popMenu.menu.add(R.string.delete_all).setOnMenuItemClickListener {
                            Toast.makeText(requireContext(), "delete_all", Toast.LENGTH_SHORT).show()
                            for (item in PictureContent.items){
                                deleteFile(item.uri!!)
                            }
                            PictureContent.items.clear()
                            binding.rv.adapter?.notifyDataSetChanged()
                            true
                        }

                        popMenu.show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        binding.rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rv.adapter = ImageAdapter(requireContext())

        binding.selectMoreImages.text = getString(R.string.select_or_capture_images)
        binding.selectMoreImages.setOnClickListener {
            selectMoreImagesClicked = true
            val dialog = BottomSheetDialog(requireContext())
            dialog.setContentView(R.layout.fragment_bottom_sheet)

            dialog.findViewById<LinearLayout>(R.id.camera)?.setOnClickListener {
                selectMoreImagesClicked = false
                dialog.dismiss()
                captureImage()
            }
            dialog.findViewById<LinearLayout>(R.id.gallery)?.setOnClickListener {
                selectMoreImagesClicked = false
                dialog.dismiss()
                captureImageFromGallery()
            }
            dialog.show()
        }

        requestAllPermissions()
    }

    private fun convertToPdf() {
        val mediaStorageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS + "/PDF")
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",Locale.ENGLISH).format(Date())
        val pdfFileName = "PDF_" + timeStamp + "_"
        val pdfExtension = ".pdf"
        val list = mutableListOf<PictureItem>()
        for (item in PictureContent.items){
            if (item.checked){
                list.add(item)
            }
        }
        if (mediaStorageDir != null && !mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return
            }
        }

        val pdfFile = File("$mediaStorageDir/$pdfFileName${pdfExtension}")
        val fileOutputStream = FileOutputStream(pdfFile)
        val pdfDoc = PdfDocument()
        var myBitmap:Bitmap? = null
        for (i in list.indices){
            val imageUri = list[i].uri
            if (Build.VERSION.SDK_INT < 28) {
                myBitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    imageUri
                )
            } else {
                val source = imageUri?.let {
                    ImageDecoder.createSource(
                        requireContext().contentResolver,
                        it
                    )
                }
                if (source != null)
                    try {
                        myBitmap = ImageDecoder.decodeBitmap(source);
                    } catch (e: IOException) {
                        val input = requireContext().contentResolver.openInputStream(imageUri)
                        myBitmap = BitmapFactory.decodeStream(input)
                        e.printStackTrace()
                    }
            }

            myBitmap = myBitmap?.copy(Bitmap.Config.ARGB_8888,false)
            val pageInfo = PageInfo.Builder(myBitmap?.width!!,myBitmap.height,i+1).create()
            val page1 = pdfDoc.startPage(pageInfo)

            val canvas = page1.canvas
            canvas.drawBitmap(myBitmap,0f,0f,null)

            pdfDoc.finishPage(page1)
            myBitmap.recycle()
        }

        pdfDoc.writeTo(fileOutputStream)
        pdfDoc.close()
    }

    private fun deleteFile(uri: Uri) {
        val fdelete: File = File(uri.path)
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + uri.path)
            } else {
                System.out.println("file not Deleted :" + uri.path)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun captureImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile()
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            requireContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile!!
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent,
                            CAPTURE_IMAGE_REQUEST
                        )
                    }
                } catch (ex: Exception) {
                    Toast.makeText(requireContext(), ex.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun captureImageFromGallery() {
        val pickPhoto = Intent()
        pickPhoto.setAction(Intent.ACTION_PICK)
        pickPhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*")

        startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"),
            CAPTURE_IMAGE_FROM_GALLERY_REQUEST
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var imageUri : Uri? = null

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAPTURE_IMAGE_REQUEST -> {
                    imageUri = Uri.fromFile(photoFile)
                    PictureContent.addItem(PictureItem(imageUri))
                    CoroutineScope(Dispatchers.IO).launch {
                        loadSavedImages(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)
                        withContext(Dispatchers.Main) {
                            binding.rv.adapter = ImageAdapter(requireContext())
                        }
                    }
                }

                CAPTURE_IMAGE_FROM_GALLERY_REQUEST -> {
                    imageUri = data?.data
                    mCurrentPhotoPath = FilePathUtil.getRealPathFromURI(activity, imageUri)

                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",Locale.ENGLISH).format(Date())
                    val imageFileName = "JPEG_" + timeStamp + "_"
                    val mediaStorageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

                    if (mediaStorageDir != null && !mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            return
                        }
                    }

                    val image = File("$mediaStorageDir/$imageFileName${imageExtension}")
                    copyFile(File(mCurrentPhotoPath), image);
                    PictureContent.addItem(PictureItem(Uri.fromFile(image)))
                    CoroutineScope(Dispatchers.IO).launch {
                        loadSavedImages(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)
                        withContext(Dispatchers.Main) {
                            binding.rv.adapter = ImageAdapter(requireContext())
                        }                    }
                }
            }
        }
        else{
            photoFile?.delete()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(sourceFile: File, destFile: File) {
        if (!sourceFile.exists()) {
            return
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        source = FileInputStream(sourceFile).channel
        destination = FileOutputStream(destFile).channel
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size())
        }
        source?.close()
        destination?.close()
    }


    private fun requestAllPermissions() {
        if ((ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_VIDEO
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_AUDIO
            ) != PackageManager.PERMISSION_GRANTED) ||
            (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            val permissions: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                )
            } else {
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            }
            requestPermissions(
                permissions, 201
            )
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                loadSavedImages(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)
                withContext(Dispatchers.Main) {
                    binding.rv.adapter = ImageAdapter(requireContext())
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 201) {
            var allGranted = true
            if (grantResults.isNotEmpty()) {
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false
                        break
                    }
                }
            }
            if (!allGranted) {
                showPermissionRequestDialog(activity)
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    loadSavedImages(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)
                    withContext(Dispatchers.Main) {
                        binding.rv.adapter = ImageAdapter(requireContext())
                    }
                }
            }
        }
    }

    private fun showPermissionRequestDialog(activity: FragmentActivity?) {

        val builder = activity?.let {

            AlertDialog.Builder(it, androidx.appcompat.R.style.AlertDialog_AppCompat)
                .setMessage("Please grant required permissions to use the application")
                .setCancelable(false)

                .setPositiveButton("OK") { dialogInterface, i ->
                    if (permissionDialog != null) {
                        permissionDialog.dismiss()
                    }
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                    activity.finish()
                }
                .setNegativeButton(
                    "CANCEL"
                ) { dialogInterface, i ->
                    if (permissionDialog != null)
                        activity.finish()
                }
        }
        permissionDialog = builder?.create()!!
        permissionDialog.show()
    }


//    private fun loadImages() {
//        val storageManager:StorageManager = requireContext().getSystemService(Context.STORAGE_SERVICE) as StorageManager
//        val storageVolume = storageManager.storageVolumes[0]
//
//        storageVolume.directory.path + "/Download/"
//    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",Locale.ENGLISH).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val mediaStorageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (mediaStorageDir != null && !mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        //for permanent storage
//        val image = File("$mediaStorageDir/$imageFileName${imageExtension}")

        //if want to save temp
        val image = File.createTempFile(
            imageFileName, /* prefix */
            imageExtension, /* suffix */
            mediaStorageDir      /* directory */
        )
        image.deleteOnExit()

        mCurrentPhotoPath = image.absolutePath
        return image
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


object PictureContent {
    var items: ArrayList<PictureItem> = ArrayList()

    fun loadSavedImages(dir: File) {
        items.clear()
        if (dir.exists()) {
            val files = dir.listFiles()
            for (file in files!!) {
                val absolutePath = file.absolutePath
                val extension = absolutePath.substring(absolutePath.lastIndexOf("."))
                if (extension.equals(".jpg",true) || extension.equals(".jpeg",true) || extension.equals(".png",true)) {
                    loadImage(file)
                }
            }
        }
    }

    fun loadImage(file: File?) {
        val newItem = PictureItem()
        newItem.uri = Uri.fromFile(file)
        addItem(newItem)
    }

    fun addItem(item: PictureItem) {
        items.add(item)
    }
}


class PictureItem(
    var uri: Uri? = null,
    var checked: Boolean = false
)

object FilePathUtil {
    fun getRealPathFromURI(context: Context?, uri: Uri?): String {
        var filePath = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)

        // Use a CursorLoader to query the data and return the path
        val cursorLoader = CursorLoader(context!!, uri!!, projection, null, null, null)
        val cursor: Cursor = cursorLoader.loadInBackground()!!
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        filePath = cursor.getString(column_index)
        cursor.close()
        return filePath
    }
}