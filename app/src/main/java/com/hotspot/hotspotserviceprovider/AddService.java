package com.hotspot.hotspotserviceprovider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hotspot.hotspotserviceprovider.modelClasses.DoctorModelClass;
import com.hotspot.hotspotserviceprovider.modelClasses.ServiceProviderModel;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class AddService extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int OPTION_CAMERA_CODE = 1000;
    private static final int OPTION_GALLERY_CODE = 1001;
    private String[] cameraPermission;
    private String[] storagePermission;
    ServiceProviderModel model;
    TextInputEditText providerName, providerContact, providerPin, doctorExperienceYear, doctorExperienceMonth, registrationNumber;
    ImageView imageView;
    Uri imageUri;
    Spinner spinnerServiceCategory, spinner1, spinner2, spinner3;
    LinearLayout spinner1Layout, spinner2Layout, spinner3Layout, spinnerLayout;
    String serviceCategory, firstCategory, secondCategory = "";
    ImageButton addImage;
    Button submit;
    String TAG = "AddService";
    String uid, phone, docQualification, profileImage, docHigherQualification, name;
    ProgressBar progressBar;
    TextView from, to;
    View doctDetailLayout;
    Spinner qualificationSpinner, higherQualificationSpinner;
    private String[] medicalCategoryList = new String[]{
            "Select Category", "Homoepathic", "Eleopathic", "Ayurvedic", "Pet Doctor"};
    private String[] partnerService = new String[]{
            "Select Service", "Doctor"};

    private String[] doctorQualification = new String[]{
            "Select Qualification",
            "Bachelor of Medicine OR \n Bachelor of Surgery (MBBS, BMBS, MBChB, MBBCh)",
            "Doctor of Medicine (MD, Dr.MuD, Dr.Med)",
            "Doctor of Osteopathic Medicine (DO)"
    };
    private String[] higherQualification = new String[]{
            "Higher Qualification(if any)",
            "Doctor of Medicine by research (MD(Res), DM)",
            "Doctor of Philosophy (PhD, DPhil)",
            "Master of Clinical Medicine (MCM)",
            "Master of Medical Science (MMSc, MMedSc)",
            "Master of Medicine (MM, MMed)",
            "Master of Philosophy (MPhil)",
            "Master of Surgery (MS, MSurg, MChir, MCh, ChM, CM)",
            "Master of Science in Medicine or Surgery (MSc)",
            "Doctor of Clinical Medicine (DCM)",
            "Doctor of Clinical Surgery (DClinSurg)",
            "Doctor of Medical Science (DMSc, DMedSc)",
            "Doctor of Surgery (DS, DSurg)"
    };
    private String[] subCategoryList = new String[]{
            "Select Category", "General Physician", "Dermatology", "Gynecology", "Sexologist", "Orthopedic", "Ent", "Neuro", "Psychiatry", "Child Specialist", "Gastroentrologist",
            "Gastroenterologist", "Cardiologist", "Diabetology", "Pulmonologist", "Oncologist", "Ophtalmologist", "Psychologist", "Urologist", "Vascular Surgeon", "Dentist", "Dietician"};
    TextView timeButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        try {
            cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            SharedPreferences pref = getSharedPreferences("PartnerPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            phone = pref.getString("Phone", "");
            uid = pref.getString("uid", "");
            profileImage = pref.getString("Profileimage", "");
            name = pref.getString("userName", "");

            doctorExperienceMonth = findViewById(R.id.doctorExperienceMonth);
            doctorExperienceYear = findViewById(R.id.doctorExperienceYear);
            registrationNumber = findViewById(R.id.registrationNumber);
            qualificationSpinner = findViewById(R.id.qualificationSpinner);
            doctDetailLayout = findViewById(R.id.doctDetailLayout);
            spinner1 = findViewById(R.id.spinner1);
            spinner2 = findViewById(R.id.spinner2);
            spinner3 = findViewById(R.id.spinner3);
            from = findViewById(R.id.fromTime);
            to = findViewById(R.id.toTime);
            higherQualificationSpinner = findViewById(R.id.higherQualificationSpinner);
            spinner1Layout = findViewById(R.id.spinner1Layout);
            spinner2Layout = findViewById(R.id.spinner2Layout);
            spinner3Layout = findViewById(R.id.spinner3Layout);
            spinnerLayout = findViewById(R.id.spinnerLayout);
            timeButton = findViewById(R.id.timeButton);
            progressBar = findViewById(R.id.progressBar);
            imageView = findViewById(R.id.imageView);
            providerName = findViewById(R.id.Serviceman);
            spinnerServiceCategory = findViewById(R.id.spinnerServicecategory);
            providerContact = findViewById(R.id.providerContact);
//    providerEmail = findViewById(R.id.providerEmail);
            providerPin = findViewById(R.id.servicePin);
            submit = findViewById(R.id.submit);


            providerName.setText(name);
            providerContact.setText(phone);

            Picasso.get().load(profileImage).into(imageView);
            ArrayAdapter<String> adapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, partnerService);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerServiceCategory.setAdapter(adapter);
            spinnerServiceCategory.setOnItemSelectedListener(this);

            ArrayAdapter<String> qualificationAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, doctorQualification);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            qualificationSpinner.setAdapter(qualificationAdapter);
            qualificationSpinner.setOnItemSelectedListener(this);

            ArrayAdapter<String> higherQualificationAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, higherQualification);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            higherQualificationSpinner.setAdapter(higherQualificationAdapter);
            higherQualificationSpinner.setOnItemSelectedListener(this);


            from.setOnClickListener(this);
            to.setOnClickListener(this);


            spinnerServiceCategory.setOnItemSelectedListener(this);
            submit.setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openTimePicker(String string) {

        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;

        if (string.equals("from")) {
            mTimePicker = new TimePickerDialog(AddService.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    String minute;
                    Log.w(TAG, "selectedMinute=>" + selectedMinute);

                    if (selectedMinute == 0) {
                        Log.w(TAG, "MInute==0");
                        minute = "00";
                    } else
                        minute = String.valueOf(selectedMinute);

                    from.setText(selectedHour + ":" + minute);
                    from.setError(null);
                }
            }, hour, minute, false);//Not 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        } else if (string.equals("to")) {
            mTimePicker = new TimePickerDialog(AddService.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    String minute;
                    if (selectedMinute == 0) {
                        Log.w(TAG, "MInute==0");
                        minute = "00";
                    } else
                        minute = String.valueOf(selectedMinute);

                    to.setText(selectedHour + ":" + minute);
                    to.setError(null);
                }
            }, hour, minute, false);//Not 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {

            case R.id.spinnerServicecategory: {
                serviceCategory = adapterView.getItemAtPosition(i).toString();

                switch (serviceCategory) {
                    case "Doctor": {
                        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                                android.R.layout.simple_spinner_item, medicalCategoryList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner1.setAdapter(adapter);
                        spinner1.setOnItemSelectedListener(this);
                        doctDetailLayout.setVisibility(View.VISIBLE);
                        spinner1Layout.setVisibility(View.VISIBLE);
                        spinnerLayout.setVisibility(View.VISIBLE);
                        break;
                    }
                    default: {
                        doctDetailLayout.setVisibility(View.GONE);
                        spinner1Layout.setVisibility(View.GONE);
                        spinner2Layout.setVisibility(View.GONE);
                        spinner2Layout.setVisibility(View.GONE);
                        spinnerLayout.setVisibility(View.GONE);

                        break;

                    }
                }

            }
            case R.id.spinner1: {

                firstCategory = adapterView.getItemAtPosition(i).toString();

                if (firstCategory.equals("Eleopathic")) {
                    ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, subCategoryList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner2.setAdapter(adapter);
                    spinner2.setOnItemSelectedListener(this);
                    spinner2Layout.setVisibility(View.VISIBLE);
                } else {
                    spinner2Layout.setVisibility(View.GONE);

                }

                break;
            }

            case R.id.spinner2: {
                secondCategory = adapterView.getItemAtPosition(i).toString();


            }

            case R.id.qualificationSpinner: {
                docQualification = adapterView.getItemAtPosition(i).toString();
                Log.w(TAG, "Qualificatin=>" + docQualification);
                break;
            }
            case R.id.higherQualificationSpinner: {
                docHigherQualification = adapterView.getItemAtPosition(i).toString();

                break;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.fromTime: {
                openTimePicker("from");


                break;
            }
            case R.id.toTime: {
                openTimePicker("to");

                break;
            }
            case R.id.submit: {
                if (providerName.getText().toString().trim().equals("")) {
                    providerName.requestFocus();
                    providerName.setError("Service Provider Name is necessary");
                }
                else if (providerPin.getText().toString().trim().equals("")) {
                    providerPin.requestFocus();
                    providerPin.setError("Service Area Pin is necessary");
                }
                 else if (serviceCategory.equals("Select Service")) {
                    Toast.makeText(AddService.this, "Service Category is necessary", Toast.LENGTH_LONG).show();

                    spinnerServiceCategory.requestFocus();
                }
                else if (firstCategory.equals("Select Category")) {
                    Toast.makeText(AddService.this, "Choose Your Categories properly", Toast.LENGTH_LONG).show();
                    spinner1.requestFocus();
                }

                else if (serviceCategory.equals("Doctor")) {
                    if(from.getText().equals("from") ){
                        from.setError("Select Availability Time");
                        from.requestFocus();
                    }
                    else if( to.getText().equals("to")){
                        to.setError("Select Availability Time");
                        to.requestFocus();
                    }else
                    if (docQualification.equals("Select Qualification")) {
                        qualificationSpinner.requestFocus();
                        Toast.makeText(getApplicationContext(), "Please Select Qualification", Toast.LENGTH_LONG).show();
                    }else
                    if (firstCategory.equals("Eleopathic") && secondCategory.equals("Select Category")) {

                            Toast.makeText(AddService.this, "Choose Your Categories properly", Toast.LENGTH_LONG).show();
                            spinner2.requestFocus();

                    }
                     else
                    if (doctorExperienceYear.getText().toString().trim().equals("") && doctorExperienceMonth.getText().toString().trim().equals("")) {
                        doctorExperienceYear.requestFocus();
                        doctorExperienceYear.setError("Experience is required");
                    }
                    else
                    if (registrationNumber.getText().toString().equals("")) {
                        registrationNumber.requestFocus();
                        registrationNumber.setError("Doctors registration number is mandatory");
                    }else{
                        Log.w(TAG, "SAVE REACHED");
                    progressBar.setVisibility(View.VISIBLE);
                    saveOnDB();}
        }

                break;
            }
        }
    }


    private void saveOnDB() {

        final DoctorModelClass doctorModelClass;
        if (serviceCategory.equals("Doctor")) {
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Partner").child(phone).child("Services");

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Doctors");
            final String pushkey = ref.push().getKey();
            final String[] path = new String[1];

            model = new ServiceProviderModel();
            doctorModelClass = new DoctorModelClass();

//        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Partner").child("Services");
//        if(imageUri!=null){
//            storageReference.child(phone).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
//                    downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            path[0] = uri.toString();
//                            Map<String, Object> imageObject = new HashMap<>();
//                            imageObject.put("providerImage", path[0]);
//                            ref.child(pushkey).updateChildren(imageObject).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    Intent intent=new Intent(AddService.this,ManageShop.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                    progressBar.setVisibility(View.GONE);
//                                    Toast.makeText(AddService.this,"Service Added.Wait for Verification",Toast.LENGTH_LONG).show();
//                                }
//                            });
//
//                        }
//                    });
//                }
//            });
//        }
            doctorModelClass.setFromTime(from.getText().toString().trim());
            doctorModelClass.setToTime(to.getText().toString().trim());
            doctorModelClass.setDoctorName(providerName.getText().toString().trim());
            doctorModelClass.setDoctorQualification(docQualification);
            if(!docHigherQualification.equals("Higher Qualification(if any)"))
                     doctorModelClass.setDoctorHigherQualification(docHigherQualification);

            doctorModelClass.setDoctorProfilePic(profileImage);
            doctorModelClass.setDoctorType(firstCategory);
            doctorModelClass.setDoctorSpecialization(secondCategory);

            doctorModelClass.setDotorExperience(doctorExperienceYear.getText().toString().trim()
                    + " years & " + doctorExperienceMonth.getText().toString().trim() + " months");
            doctorModelClass.setDoctorRegistrationId(registrationNumber.getText().toString().trim());
            doctorModelClass.setPushkey(pushkey);
            doctorModelClass.setUid(uid);
            doctorModelClass.setRegisteredPhone(phone);
            doctorModelClass.setVerificationStatus(false);
            doctorModelClass.setServicType("Doctor");
            ref.child(pushkey).setValue(doctorModelClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddService.this, "Service Added", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    reference.child(pushkey).setValue(doctorModelClass);
                }
            });


//        model.setProviderCategory(serviceCategory);
//        model.setPushkey(pushkey);
//        model.setProviderNumber(providerContact.getText().toString().trim());
//        model.setProviderPincode(providerPin.getText().toString().trim());

//        ref.child(pushkey).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Intent intent=new Intent(AddService.this,ManageShop.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });


        }

    }


}