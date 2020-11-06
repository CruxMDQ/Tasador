package com.callisto.tasador.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.callisto.tasador.CODE_CONTACTS
import com.callisto.tasador.CODE_EXTERNAL_STORAGE
import com.callisto.tasador.R
import com.callisto.tasador.UNINITIALIZED_ID
import com.callisto.tasador.database.getDatabase
import com.callisto.tasador.databinding.FragmentPlotCreationBinding
import com.callisto.tasador.viewmodels.PlotEditionViewModel
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class PlotEditionFragment : Fragment()
{
    private var parcelId: Int = UNINITIALIZED_ID

    private var parentId: Int = UNINITIALIZED_ID

    /** To lazily initialize this, it must not be referenced before onActivityCreated.
     */
    private val viewModel: PlotEditionViewModel by lazy {
    //
        val activity = requireNotNull(this.activity)
        {
            "You can only access the ViewModel after onActivityCreated"
        }

        /**
         * Different model initializers are required depending on whether the real estate object
         * in question has a parent or not
         */
        if (parentId == UNINITIALIZED_ID)
        {
            PlotEditionViewModel.Factory(
                parcelId,
                getDatabase(activity.applicationContext).RealtorDao,
                activity.application)
                .create(
                    //
                    PlotEditionViewModel::class.java
                )
        }
        else
        {
            PlotEditionViewModel.FactoryWithParent(
                parcelId,
                parentId,
                getDatabase(activity.applicationContext).RealtorDao,
                activity.application)
                .create(
                    //
                    PlotEditionViewModel::class.java
                )
        }
    }

    private lateinit var binding: FragmentPlotCreationBinding

    /** Instantiates user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI. (That's what binding.root
     * actually is.)
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_plot_creation,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        /**
         * This fragment takes two arguments:
         * - parcelId: self-explanatory
         * - parentId: identifier of the real estate serving as parent to this one
         */
        val arguments = arguments?.let { PlotEditionFragmentArgs.fromBundle(it) }

        parcelId = arguments!!.propertyId

        parentId = arguments.parentId

        viewModel.parentId = parentId

        setUpRealEstateLookUpObserver()

        setUpPersistenceObserver()

        setUpPDFExportObserver()

        setUpPlotSurfaceGubbinz()

        setUpContactBookGubbinz()

        return binding.root
    }

    private fun setUpContactBookGubbinz()
    {
        binding.panelContentOwnerData.setOnClickListener {
        //
            // DONE This should fire once it's checked that the app has permission, NOT BEFORE.
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                requireActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            )
            {
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), CODE_CONTACTS)
            }
            else
            {
                startContactBookActivity()
            }
        }
    }

    private fun startContactBookActivity()
    {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, CODE_CONTACTS)
    }

    private fun setUpPlotSurfaceGubbinz()
    {
        binding.chkRegular.setOnCheckedChangeListener { _, isChecked ->
            //
            if (isChecked)
            {
                binding.editFront.isEnabled = true
                binding.editSide.isEnabled = true

                binding.editArea.isEnabled = false
            }
            else
            {
                binding.editFront.isEnabled = false
                binding.editFront.setText("0.0")

                binding.editSide.isEnabled = false
                binding.editSide.setText("0.0")

                binding.editArea.isEnabled = true
            }
        }

        val areaTextWatcher = object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?)
            {
                if (binding.chkRegular.isChecked)
                {
                    if
                    (
                        binding.editFront.text.toString().isNotEmpty() &&
                        binding.editSide.text.toString().isNotEmpty()
                    )
                    {
                        val front = binding.editFront.text.toString().toFloat()
                        val side = binding.editSide.text.toString().toFloat()

                        val area = (front * side).toString()

                        binding.editArea.setText(area)
                    }
                }
            }
        }

        binding.editFront.addTextChangedListener(areaTextWatcher)
        binding.editSide.addTextChangedListener(areaTextWatcher)

        binding.chkRegular.isChecked = true
    }

    /**
     * The flag observed here changes value on the content saving step.
     */
    private fun setUpPDFExportObserver()
    {
        viewModel.isExportingData.observe(viewLifecycleOwner, {
        //
            if (it)
            {
                showDataExportDialog()
            }
        })
    }

    /**
     * This observer is triggered by means of clicking the single big button on the screen.
     *
     * 11/6/20: Why do it like this instead of just using a ClickListener or an onClick field
     * on the XML?
     */
    private fun setUpPersistenceObserver()
    {
        viewModel.isSavingData.observe(viewLifecycleOwner, {
            //
            if (it)
            {
                viewModel.launchAddParcel(
                    //
                    binding.editFront.text.toString(),
                    binding.editSide.text.toString(),
                    binding.editArea.text.toString(),
                    binding.editCataster.text.toString(),
                    binding.editZonification.text.toString(),
                    binding.editRoadType.text.toString(),
                    binding.editTaxId.text.toString(),
                    binding.chkUtilityPower.isChecked,
                    binding.chkUtilityWater.isChecked,
                    binding.chkUtilityDrains.isChecked,
                    binding.chkUtilityNatgas.isChecked,
                    binding.chkUtilitySewers.isChecked,
                    binding.chkUtilityInternet.isChecked,
                    "",
                    binding.txtOwnerName.text.toString(),
                    binding.editPriceFinal.text.toString().toLong(),
                    binding.editPriceQuoted.text.toString().toLong()
                )
            }
        })
    }

    /**
     * Look up the data for this particular real estate.
     * Returns nothing if a new one is being created.
     */
    private fun setUpRealEstateLookUpObserver()
    {
        viewModel.repository.properties.observe(viewLifecycleOwner, {
            //
            viewModel.setActiveParcel(it.find { item -> item.id == parcelId })
        })
    }

    private fun showDataExportDialog()
    {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setTitle(getText(R.string.msg_export_data))
        alertDialogBuilder.setMessage(getText(R.string.msg_export_data_hint))

        alertDialogBuilder.setPositiveButton(getText(R.string.btn_yes)) { dialog, _ ->
        //
            launchDataExport(requireActivity())

            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton(getText(R.string.btn_no)) { dialog, _ ->
        //
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()
    }

    private fun launchDataExport(requiredActivity: FragmentActivity)
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        {
            //system OS >= Marshmallow(6.0), check permission is enabled or not
            if (ContextCompat.checkSelfPermission(
                    requiredActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED)
            {
                //permission was not granted, request it
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(
                    requiredActivity,
                    permissions,
                    CODE_EXTERNAL_STORAGE
                )
            }
            else
            {
                //permission already granted, call savePdf() method
                savePdf(requiredActivity)
            }
        }
        else
        {
            //system OS < marshmallow, call savePdf() method
            savePdf(requiredActivity)
        }
    }

    private fun savePdf(requiredActivity: FragmentActivity)
    {
        //create object of Document class
        val mDoc = Document()

        //pdf file name
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())

        //pdf file path
        val mFilePath = requiredActivity.getExternalFilesDir(null).toString() + "/" + mFileName +".pdf"

        try {
            //create instance of PdfWriter class
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))

            //open the document for writing
            mDoc.open()

            val mText = viewModel.activeParcel.value.toString()

            //add paragraph to the document
            mDoc.add(Paragraph(mText))

            //close document
            mDoc.close()

            //show file saved message with file name and path
            Toast.makeText(requiredActivity, "$mFileName.pdf\nis saved to\n$mFilePath", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception)
        {
            //if anything goes wrong causing exception, get and show exception message
            Toast.makeText(requiredActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        when(requestCode)
        {
            CODE_EXTERNAL_STORAGE ->
            {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //permission from popup was granted, call savePdf() method
                    savePdf(requireActivity())
                }
                else
                {
                    //permission from popup was denied, show error message
                    Toast.makeText(requireActivity(), "Permission denied...!", Toast.LENGTH_SHORT).show()
                }
            }
            CODE_CONTACTS ->
            {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startContactBookActivity()
                }
                else
                {
                    Toast.makeText(requireActivity(), "Permission denied...!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CODE_CONTACTS && resultCode == Activity.RESULT_OK)
        {
            var name = ""

            var phoneNumValue = ""

            val contactData = data!!.data

            val contentResolver = requireActivity().contentResolver

            val cursor = contactData?.let { contentResolver.query(it, null, null, null, null) }

            if (cursor!!.count > 0)
            {
                while (cursor.moveToNext())
                {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    val phoneNumber = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                    ).toInt()

                    if (phoneNumber > 0)
                    {
                        val cursorPhone = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                            arrayOf(id),
                            null
                        )

                        if (cursorPhone!!.count > 0)
                        {
                            while (cursorPhone.moveToNext())
                            {
                                phoneNumValue = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            }
                        }
                        cursorPhone.close()
                    }
                }
                setOwnerInfo(name, phoneNumValue)
            }
            else
            {
                Toast.makeText(requireContext(), "No contacts available!", Toast.LENGTH_SHORT).show()
            }

            cursor.close()
        }
    }

    private fun setOwnerInfo(name: String?, phoneNumValue: String?)
    {
        binding.txtOwnerName.text = name
        binding.txtOwnerPhone.text = phoneNumValue
    }
}