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
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import brainmetics.com.contactdatabase.domain.ContactPerson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.width;

public class InputActivity extends AppCompatActivity {

    @BindView(R.id.txtFullname)
    EditText txtFullname;

    @BindView(R.id.txtPhone)
    EditText txtPhone;

    @BindView(R.id.txtEmail)
    EditText txtEmail;

    @BindView(R.id.txtAddress)
    EditText txtAddress;

    @BindView(R.id.imgPhoto)
    ImageView imgPhoto;

    private static final int REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        ButterKnife.bind(this);

        if(shouldAskPermission()){
            askPermission();
        }
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
        ContactPerson cp = new ContactPerson();
        cp.setFullName(txtFullname.getText().toString().trim());
        cp.setPhone(txtPhone.getText().toString().trim());
        cp.setEmail(txtEmail.getText().toString().trim());
        cp.setAddress(txtAddress.getText().toString().trim());
        cp.setPhoto("");
        cp.save();
        txtFullname.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        Toast.makeText(this,"Data tersimpan",Toast.LENGTH_SHORT).show();
        finish();
    }
}
