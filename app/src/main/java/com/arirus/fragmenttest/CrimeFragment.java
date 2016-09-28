package com.arirus.fragmenttest;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.arirus.fragmenttest.Utils.PictureUtils;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by whd910421 on 16/7/21.
 */

/*
* 一个Crime的详细信息展示Fragment
* */
public class CrimeFragment extends Fragment implements View.OnClickListener {
    private  Crime mCrime;
    private File mPhotoFiles;
    private EditText mTitleField;
    private Button mDataButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mReportButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Callbacks mCallbacks;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_PHOTO = "DialogPHOTO";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 3;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    public static CrimeFragment newInstance(UUID crimeId) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeid = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getContext()).getCrime(crimeid);
        mPhotoFiles = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        System.out.println("onCreate走了一次"+crimeid.toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_instance,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_item_del_crime :
                CrimeLab.get(getContext()).removeCrime(mCrime);
                getActivity().finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK) return;
        if(requestCode == REQUEST_DATE)
        {
            Date date = (Date) data.getSerializableExtra(DataPickerFragement.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        }
        else if (requestCode == REQUEST_CONTACT)
        {
            Uri contacturi = data.getData();
            String[] queryFiles = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID
            };

            Cursor c = getActivity().getContentResolver().query(contacturi,queryFiles,null,null,null);

            try
            {
                if(c.getCount() == 0)
                    return;

                c.moveToFirst();
                String suspect = c.getString(0); //0 对应了queryFiles里面的第1个参数
                long id = c.getLong(1);
                mCrime.setSuspect(suspect);
                mCrime.setContactID(id);
                updateCrime();
                mSuspectButton.setText(suspect);
                UpdateDailBtn();
            }
            finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
            updateCrime();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button :
                FragmentManager fragmentManager = getFragmentManager();
                DataPickerFragement dialog = DataPickerFragement.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(fragmentManager,DIALOG_DATE);
                break;
            case R.id.button_send_msg :
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                i = Intent.createChooser(i, getString(R.string.send_report)); //创建应用选择窗口
                Intent i = ShareCompat.IntentBuilder.from(getActivity()).setSubject(getString(R.string.crime_report_subject))
                        .setType("text/plain").setText(getCrimeReport()).getIntent();

                startActivity(i);
                break;
            case R.id.button_open_app :
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    startActivityForResult(pickContact, REQUEST_CONTACT);
                }
                break;
            case R.id.button_call:
//                if ( mSuspectID == null ) return;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//                } else {
                ShowTel();
                break;
//                }
            case R.id.crime_camera:
                startActivityForResult(captureImage, REQUEST_PHOTO);
                break;
            case R.id.crime_photo:
                DetailPhotoFragement photoView = DetailPhotoFragement.newInstance(mCrime.getId());
                photoView.show(getFragmentManager(), DIALOG_PHOTO);
                break;
            default:
                return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        }
    }

    private void UpdateDailBtn()
    {
        if (mCrime.getContactID()!=0)
            mCallButton.setEnabled(true);
        else
            mCallButton.setEnabled(false);
    }

    private void ShowTel()
    {
        Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String selectClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";

        String[] fields = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String[] selectParams = {Long.toString(mCrime.getContactID())};

        Cursor cursor = getActivity().getContentResolver().query(contentUri, fields, selectClause, selectParams, null);

        if(cursor != null && cursor.getCount() > 0)
        {
            try
            {
                cursor.moveToFirst();

                String number = cursor.getString(0);

                Uri phoneNumber = Uri.parse("tel:" + number);

                Intent intent = new Intent(Intent.ACTION_DIAL, phoneNumber);

                startActivity(intent);
            }
            finally
            {
                cursor.close();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);
        mDataButton = (Button)v.findViewById(R.id.button);
        updateDate();
        mDataButton.setOnClickListener(this);

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.checkBox);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
                updateCrime();
            }
        });

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mReportButton = (Button) v.findViewById(R.id.button_send_msg);
        mReportButton.setOnClickListener(this);

        mSuspectButton = (Button) v.findViewById(R.id.button_open_app);
        mSuspectButton.setOnClickListener(this);
        if (mCrime.getSuspect()!=null) mSuspectButton.setText(mCrime.getSuspect());


        //如果没有合适的打开应用,则不显示按钮
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY ) == null)
        {
            mSuspectButton.setEnabled(false);
        }

        mCallButton = (Button) v.findViewById(R.id.button_call);
        mCallButton.setOnClickListener(this);
        UpdateDailBtn();

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        //有文件名,并且有相机应用
        boolean canTakePhoto = (mPhotoFiles != null && captureImage.resolveActivity(packageManager) != null);
        mPhotoButton.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFiles);
            //将文件放置在外部存储,并设置其"定位"
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(this);

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(this);
        updatePhotoView();
        return v;
    }


    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updateDate() {
        mDataButton.setText(mCrime.getDate().toString());
    }

    private void updatePhotoView() {
        if (mPhotoButton == null || !mPhotoFiles.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFiles.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private String getCrimeReport()
    {
        String solvedString = null;
        if (mCrime.isSolved())
        {
            solvedString = getString(R.string.crime_report_solved);
        }
        else
        {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null)
        {
            suspect = getString(R.string.crime_report_no_suspect);
        }
        else
        {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(),dateString,solvedString,suspect);

        return  report;
    }


}
