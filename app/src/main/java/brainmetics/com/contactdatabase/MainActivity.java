package brainmetics.com.contactdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import brainmetics.com.contactdatabase.adapter.ContactAdapter;
import brainmetics.com.contactdatabase.domain.ContactPerson;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.listContact)
    ListView listContact;

    List<ContactPerson> contacts = new ArrayList<ContactPerson>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        registerForContextMenu(listContact);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.menuDelete:
                ContactPerson cp = (ContactPerson)
                        listContact.getItemAtPosition(info.position);
                cp.delete();
                Toast.makeText(this,"Contact deleted",Toast.LENGTH_LONG).show();
                loadContact();
                return  true;
            default: return super.onContextItemSelected(item);
        }
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

    @OnItemClick(R.id.listContact)
    public void setListContactOnClick(int position){
        ContactPerson cp = (ContactPerson)listContact.getItemAtPosition(position);
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra("ID", cp.getId());
        startActivity(intent);
    }

}
