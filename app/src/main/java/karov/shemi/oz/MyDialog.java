package karov.shemi.oz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MyDialog extends DialogFragment {
	public static MyDialog newInstance( int title, int defaultText ) {
		MyDialog frag = new MyDialog( );
        Bundle args = new Bundle( );
        args.putInt( "title", title );
        args.putInt( "defaultText", defaultText );
        frag.setArguments( args );
        return frag;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate  (R.layout.dialog_gps, null, false);
        View tv1 = v.findViewById(R.id.dialogtitle);
        ((TextView)tv1).setText(getResources().getString(getArguments( ).getInt( "title" )));
        View tv2 = v.findViewById(R.id.dialogdetails);
        ((TextView)tv2).setText(getResources().getString(getArguments( ).getInt( "defaultText" )));
    	
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
        // Add action buttons
               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	   mListener.onDialogPositiveClick(MyDialog.this);
                   }
               })
               .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(MyDialog.this);
                        MyDialog.this.getDialog().cancel();
                   }
               });      
        return builder.create();
    }
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    }


