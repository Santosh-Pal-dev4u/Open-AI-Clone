package com.Santoshpal_OpenAI;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Dall_e extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private EditText inputText;
    private TextView generateBtn,textView;
    private ProgressBar progressBar;
    private ImageView imageView1, imageView2, imageView3, imageView4;



  LinearLayout layout;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.lightgrey));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dall_e);

        inputText = findViewById(R.id.editText_dall_e);
        generateBtn = findViewById(R.id.generate_btn);
        progressBar = findViewById(R.id.progress_bar);
        imageView1 = findViewById(R.id.generative_image1);
        imageView2 = findViewById(R.id.generative_image2);
        imageView3 = findViewById(R.id.generative_image3);
        imageView4 = findViewById(R.id.generative_image4);
        layout=findViewById(R.id.image_collection);
        textView=findViewById(R.id.download);

            generateBtn.setOnClickListener((v)->{
                String text = inputText.getText().toString().trim();
                if(text.isEmpty()){
                    inputText.setError("Text can't be empty");
                    return;
                }
                callAPI(text);
            });

        }

        void callAPI(String text){
            //API CALL
            setInProgress(true);
            JSONObject jsonBody = new JSONObject();
            try{
                jsonBody.put("prompt",text);
                jsonBody.put("size","256x256");
                jsonBody.put("n",4);

            }catch (Exception e){
                e.printStackTrace();
            }

            RequestBody requestBody = RequestBody.create(jsonBody.toString(),JSON);

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/images/generations")
                    .header("Authorization","Bearer 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")
                    .post(requestBody)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Toast.makeText(getApplicationContext(),"Failed to generate image", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {



                    try{
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String imageUrl = jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                        String imageUrl1 = jsonObject.getJSONArray("data").getJSONObject(1).getString("url");
                        String imageUrl2 = jsonObject.getJSONArray("data").getJSONObject(2).getString("url");
                        String imageUrl3 = jsonObject.getJSONArray("data").getJSONObject(3).getString("url");
                        loadImage(imageUrl,imageUrl1,imageUrl2,imageUrl3);
                        setInProgress(false);


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }

        void setInProgress(boolean inProgress){
            runOnUiThread(()->{
                if(inProgress){
                    progressBar.setVisibility(View.VISIBLE);
                    generateBtn.setVisibility(View.GONE);

                }else{
                    progressBar.setVisibility(View.GONE);
                    generateBtn.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.VISIBLE);

                }
            });

        }

        void loadImage(String url,String url1,String url2,String url3){
            //load image

            runOnUiThread(()->{
                Glide.with(getApplicationContext()).load(url).into(imageView1);
                Glide.with(getApplicationContext()).load(url1).into(imageView2);
                Glide.with(getApplicationContext()).load(url2).into(imageView3);
                Glide.with(getApplicationContext()).load(url3).into(imageView4);


                // Set click listeners for the images
                imageView1.setOnClickListener(v -> downloadImage(url));
                imageView2.setOnClickListener(v -> downloadImage(url1));
                imageView3.setOnClickListener(v -> downloadImage(url2));
                imageView4.setOnClickListener(v -> downloadImage(url3));

            });

        }
    void downloadImage(String imageUrl) {
        // Download the image and save it to the gallery
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Save the image to the gallery
                        saveImageToGallery(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Implementation not needed
                    }
                });
    }

    void saveImageToGallery(Bitmap imageBitmap) {
        // Save the image to the gallery
        OutputStream fos;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_" + System.currentTimeMillis() + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            try {
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                if (fos != null) {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Toast.makeText(getApplicationContext(), "Image downloaded and saved to gallery", Toast.LENGTH_SHORT).show();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File imageFile = new File(imagesDir, "Image_" + System.currentTimeMillis() + ".jpg");

            try {
                fos = new FileOutputStream(imageFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                Toast.makeText(getApplicationContext(), "Image downloaded and saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    }


