package brainmetics.com.contactdatabase.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import brainmetics.com.contactdatabase.R;
import brainmetics.com.contactdatabase.domain.ContactPerson;

/**
 * Created by Hendro Steven on 14/11/2017.
 */

public class ContactAdapter extends ArrayAdapter<ContactPerson> {
    Context context;
    List<ContactPerson> data = new ArrayList<ContactPerson>();

    public ContactAdapter(Context context, List<ContactPerson> objects) {
        super(context, R.layout.list_contact, objects);
        this.context = context;
        this.data = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_contact, parent, false);
        TextView fullName = (TextView)rowView.findViewById(R.id.txtFullname);
        TextView phone = (TextView)rowView.findViewById(R.id.txtPhone);
        TextView email = (TextView)rowView.findViewById(R.id.txtEmail);
        ImageView photo = (ImageView) rowView.findViewById(R.id.imgPhoto);
        ContactPerson cp = data.get(position);
        fullName.setText(cp.getFullName());
        phone.setText(cp.getPhone());
        email.setText(cp.getEmail());
        if(cp.getPhoto().trim().length()>0){
            Bitmap bmp = decodeBase64(cp.getPhoto());
            photo.setImageBitmap(bmp);
        }
        return rowView;
    }

    private Bitmap decodeBase64(String input){
        byte[] decodeBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodeBytes, 0, decodeBytes.length);
    }
}