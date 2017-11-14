package brainmetics.com.contactdatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import brainmetics.com.contactdatabase.domain.ContactPerson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        ButterKnife.bind(this);
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
