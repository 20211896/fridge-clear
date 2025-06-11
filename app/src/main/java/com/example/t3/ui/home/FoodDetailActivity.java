package com.example.t3.ui.home;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.t3.R;
import com.example.t3.utils.CustomToast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class FoodDetailActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private ImageView foodImageView;
    private Button addImageButton;
    private EditText editTextFood, editTextBuyDate, editTextEndDate, editTextAmount;
    private Button saveButton;
    private ImageButton backButton;
    private TextView titleText;
    private SharedPreferences sharedPreferences;
    private int cellNumber;

    // 최신 ActivityResultLauncher 사용
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foods);

        // 칸 번호 받기
        cellNumber = getIntent().getIntExtra("cell_number", 1);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("fridge_data", Context.MODE_PRIVATE);

        // ActivityResultLauncher 초기화
        initActivityLaunchers();

        initViews();
        setupClickListeners();
        loadSavedData();
    }

    private void initActivityLaunchers() {
        // 카메라 런처
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            if (extras != null) {
                                Bitmap bitmap = (Bitmap) extras.get("data");
                                if (bitmap != null) {
                                    setImageWithAutoResize(bitmap);
                                }
                            }
                        }
                    }
                });

        // 갤러리 런처
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            if (imageUri != null) {
                                try {
                                    Bitmap bitmap;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                        android.graphics.ImageDecoder.Source source =
                                                android.graphics.ImageDecoder.createSource(getContentResolver(), imageUri);
                                        bitmap = android.graphics.ImageDecoder.decodeBitmap(source);
                                    } else {
                                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                    }
                                    setImageWithAutoResize(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    CustomToast.show(FoodDetailActivity.this, "이미지를 불러올 수 없습니다");
                                }
                            }
                        }
                    }
                });
    }

    private void initViews() {
        foodImageView = findViewById(R.id.imageView3);
        addImageButton = findViewById(R.id.addImageButton);
        editTextFood = findViewById(R.id.editTextFood);
        editTextBuyDate = findViewById(R.id.editTextBuyDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextAmount = findViewById(R.id.editTextAmount);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);

        titleText.setText("칸" + cellNumber + " 정보");
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        addImageButton.setOnClickListener(v -> showImagePickerDialog());

        editTextBuyDate.setOnClickListener(v -> showDatePicker(editTextBuyDate));

        editTextEndDate.setOnClickListener(v -> showDatePicker(editTextEndDate));

        saveButton.setOnClickListener(v -> saveFoodData());
    }

    private void loadSavedData() {
        String foodName = sharedPreferences.getString("cell_" + cellNumber + "_food", "");
        String buyDate = sharedPreferences.getString("cell_" + cellNumber + "_buy_date", "");
        String endDate = sharedPreferences.getString("cell_" + cellNumber + "_end_date", "");
        String amount = sharedPreferences.getString("cell_" + cellNumber + "_amount", "");

        editTextFood.setText(foodName);
        editTextBuyDate.setText(buyDate);
        editTextEndDate.setText(endDate);
        editTextAmount.setText(amount);

        loadSavedImage();
        updateImageButtonText();
    }

    private void loadSavedImage() {
        String imageString = sharedPreferences.getString("cell_" + cellNumber + "_image", "");
        android.util.Log.d("ImageLoad", "칸" + cellNumber + " 이미지 로드 시도: " + (!imageString.isEmpty()));

        if (!imageString.isEmpty()) {
            try {
                byte[] imageBytes = android.util.Base64.decode(imageString, android.util.Base64.DEFAULT);
                Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                if (bitmap != null) {
                    android.util.Log.d("ImageLoad", "이미지 로드 성공: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                    foodImageView.setImageBitmap(bitmap);

                    foodImageView.post(this::updateImageButtonText);
                } else {
                    android.util.Log.e("ImageLoad", "Bitmap 변환 실패");
                }
            } catch (Exception e) {
                android.util.Log.e("ImageLoad", "이미지 로드 오류: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            android.util.Log.d("ImageLoad", "저장된 이미지 없음");
        }
    }

    private void updateImageButtonText() {
        String savedImage = sharedPreferences.getString("cell_" + cellNumber + "_image", "");

        if (!savedImage.isEmpty() || (foodImageView.getDrawable() != null &&
                foodImageView.getDrawable().getConstantState() != null)) {
            addImageButton.setText("사진 수정");
        } else {
            addImageButton.setText("사진 추가");
        }
        addImageButton.setCompoundDrawables(null, null, null, null);
    }

    private void showImagePickerDialog() {
        String[] options = {"카메라로 촬영", "갤러리에서 선택"};

        new AlertDialog.Builder(this)
                .setTitle("사진 선택")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermissionAndCapture();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, y, m, d) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m+1, d);
            editText.setText(selectedDate);
        }, year, month, day).show();
    }

    private void saveFoodData() {
        String foodName = editTextFood.getText().toString().trim();
        String buyDate = editTextBuyDate.getText().toString().trim();
        String endDate = editTextEndDate.getText().toString().trim();
        String amount = editTextAmount.getText().toString().trim();

        if (foodName.isEmpty()) {
            CustomToast.show(this, "음식명을 입력해주세요");
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cell_" + cellNumber + "_food", foodName);
        editor.putString("cell_" + cellNumber + "_buy_date", buyDate);
        editor.putString("cell_" + cellNumber + "_end_date", endDate);
        editor.putString("cell_" + cellNumber + "_amount", amount);

        saveImageToStorage();

        editor.apply();

        CustomToast.show(this, "칸" + cellNumber + " 정보가 저장되었습니다!");
        finish();
    }

    private void saveImageToStorage() {
        if (foodImageView.getDrawable() != null) {
            try {
                android.graphics.drawable.Drawable drawable = foodImageView.getDrawable();

                Bitmap bitmap;
                if (drawable instanceof android.graphics.drawable.BitmapDrawable) {
                    bitmap = ((android.graphics.drawable.BitmapDrawable) drawable).getBitmap();
                } else {
                    int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : foodImageView.getWidth();
                    int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : foodImageView.getHeight();
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                }

                saveBitmapToPreferences(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                CustomToast.show(this, "이미지 저장에 실패했습니다");
            }
        }
    }

    private void saveBitmapToPreferences(Bitmap bitmap) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageString = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cell_" + cellNumber + "_image", imageString);
            editor.commit();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
            CustomToast.show(this, "이미지 저장에 실패했습니다");
        }
    }

    private void setImageWithAutoResize(Bitmap bitmap) {
        int maxWidth = 800;
        int maxHeight = 600;

        if (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight) {
            float ratio = Math.min((float) maxWidth / bitmap.getWidth(), (float) maxHeight / bitmap.getHeight());
            int newWidth = Math.round(bitmap.getWidth() * ratio);
            int newHeight = Math.round(bitmap.getHeight() * ratio);
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        }

        foodImageView.setImageBitmap(bitmap);
        updateImageButtonText();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                CustomToast.show(this, "카메라 권한이 필요합니다");
            }
        }
    }
}
