package brainmetics.com.contactdatabase;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import brainmetics.com.contactdatabase.domain.ContactPerson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class InputActivity extends AppCompatActivity
        implements Validator.ValidationListener {

    @BindView(R.id.txtFullname)
    @NotEmpty
    EditText txtFullname;

    @BindView(R.id.txtPhone)
    @NotEmpty
    EditText txtPhone;

    @BindView(R.id.txtEmail)
    @NotEmpty
    @Email
    EditText txtEmail;

    @BindView(R.id.txtAddress)
    @NotEmpty
    EditText txtAddress;

    @BindView(R.id.imgPhoto)
    ImageView imgPhoto;

    private static final int REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private Bitmap bitmap;

    Validator validator;
    boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);

        if(shouldAskPermission()){
            askPermission();
        }
    }

    @Override
    public void onValidationSucceeded(){
        isValid = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors){
        for(ValidationError err: errors){
            View view  = err.getView();
            String message = err.getCollatedErrorMessage(this);
            isValid = false;
            if(view instanceof EditText){
                ((EditText) view).setError(message);
            }else{
                Toast.makeText(this,message,Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean validate(){
        if(validator!=null){
            validator.validate();;
        }
        return isValid;
    }

    protected boolean shouldAskPermission(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermission(){
        String[] permissions = {
                "android.permission.CAMERA",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        requestPermissions(permissions,200);
    }

    @OnClick(R.id.imgPhoto)
    public void imgPhotoOnClick(){
        PopupMenu popup = new PopupMenu(this, imgPhoto, Gravity.CENTER);
        try{
            Field[] fields = popup.getClass().getDeclaredFields();
            for(Field field: fields){
                if("mPopup".equals(field.getName())){
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class
                            .forName(menuPopupHelper
                                    .getClass()
                                    .getName());
                    Method setForceIcons = classPopupHelper
                            .getMethod("setForceShowIcon",boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString().trim().toUpperCase()){
                    case "PILIH GALLERY":
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent,REQUEST_CODE);
                        break;
                    case  "GUNAKAN CAMERA":
                        Intent cameraIntent =
                                new Intent(MediaStore
                                        .ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,CAMERA_REQUEST);
                        break;
                    default: break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            try{
                if(bitmap!=null){
                    bitmap.recycle();
                }
                stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                bitmap = getResizeBitmap(bitmap, 200, 200);
                imgPhoto.setImageBitmap(bitmap);
            }catch(FileNotFoundException ex){
                ex.printStackTrace();
            }finally {
                if(stream != null){
                    try{
                        stream.close();
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }else if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            if(bitmap!=null){
                bitmap.recycle();
            }
            bitmap = (Bitmap)data.getExtras().get("data");
            bitmap = getResizeBitmap(bitmap, 200, 200);
            imgPhoto.setImageBitmap(bitmap);
        }
    }

    private String encodeToBase64(Bitmap image,
                                  Bitmap.CompressFormat format,
                                  int quality){
        ByteArrayOutputStream byteArrayOs = new ByteArrayOutputStream();
        image.compress(format, quality, byteArrayOs);
        return Base64.encodeToString(byteArrayOs.toByteArray(), Base64.DEFAULT);
    }

    private Bitmap getResizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float)newWidth/width);
        float scaleHeight = ((float)newHeight/height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        Bitmap resizedBitmap = Bitmap
                .createBitmap(bitmap,0,0,width,height,matrix,false);
        bitmap.recycle();
        return resizedBitmap;
    }

    @OnClick(R.id.btnSimpan)
    public void btnSimpanOnClick(){
        if(validate()) {
            ContactPerson cp = new ContactPerson();
            cp.setFullName(txtFullname.getText().toString().trim());
            cp.setPhone(txtPhone.getText().toString().trim());
            cp.setEmail(txtEmail.getText().toString().trim());
            cp.setAddress(txtAddress.getText().toString().trim());
            if (bitmap != null) {
                cp.setPhoto(encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 80));
            } else {
                cp.setPhoto("");
            }
            cp.save();
            txtFullname.setText("");
            txtPhone.setText("");
            txtEmail.setText("");
            txtAddress.setText("");
            Toast.makeText(this, "Data tersimpan", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


}
