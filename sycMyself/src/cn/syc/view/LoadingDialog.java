package cn.syc.view;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.syc.R;

public class LoadingDialog {	
	
	/**
	 * progressDialog
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog createLoadingDialog(Context context, String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.syc_dialog_loading, null);
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);		
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);	
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.dialog_loading_animation);		
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);
		Dialog loadingDialog = new Dialog(context, R.style.syc_Dialog_style);
		loadingDialog.setCancelable(true);
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		return loadingDialog;

	}

}
