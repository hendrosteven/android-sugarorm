package brainmetics.com.contactdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import brainmetics.com.contactdatabase.adapter.ContactAdapter;
import brainmetics.com.contactdatabase.domain.ContactPerson;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.listContact)
    ListView listContact;

    List<ContactPerson> contacts = new ArrayList<ContactPerson>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menuAdd:
                Intent intent = new Intent(this,InputActivity.class);
                startActivity(intent);
                break;
            default: break;
        }
        return true;
    }

    private void loadContact(){
        contacts = ContactPerson.listAll(ContactPerson.class);
        listContact.setAdapter(new ContactAdapter(this,contacts));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContact();
    }


}
