package sydatit.ptit.btlandroid.dialog;



import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;


import sydatit.ptit.btlandroid.R;

public class LoadingDiaglog {
    private Context context;
    private Dialog dialog;

    public LoadingDiaglog() {
    }

    public LoadingDiaglog(Context context) {
        this.context = context;
    }

    public void showDialog(String title){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvTitle = dialog.findViewById(R.id.tvProcessBar);

        tvTitle.setText(title);
        dialog.create();
        dialog.show();
    }

    public void hideDialog(){
        dialog.dismiss();
    }

}
