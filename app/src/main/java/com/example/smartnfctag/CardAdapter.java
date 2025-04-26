package com.example.smartnfctag;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartnfctag.SubFunctionality.BrightnessLowHigh;
import com.example.smartnfctag.SubFunctionality.LocationAddressMe;
import com.example.smartnfctag.SubFunctionality.MakeCall;
import com.example.smartnfctag.SubFunctionality.NavigateApp;
import com.example.smartnfctag.SubFunctionality.NfcAnimationActivity;
import com.example.smartnfctag.SubFunctionality.PdfViewerActivity;
import com.example.smartnfctag.SubFunctionality.ReadMessage;
import com.example.smartnfctag.SubFunctionality.SendSMS;
import com.example.smartnfctag.SubFunctionality.SetAlaram;
import com.example.smartnfctag.SubFunctionality.ShareContact;
import com.example.smartnfctag.SubFunctionality.SilientOffOn;
import com.example.smartnfctag.SubFunctionality.SocialMedial;
import com.example.smartnfctag.SubFunctionality.WifiOff;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardItem> cardList;

    public CardAdapter(List<CardItem> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_image_desc, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem item = cardList.get(position);
        holder.cardText.setText(item.getTitle());
        holder.cardImage.setImageResource(item.getImageResId());

        // Handle click events on the card
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Clicked " + position, Toast.LENGTH_SHORT).show();
                switch (position){
                    case 0:
                        // call SilientOffOn activity
                        v.getContext().startActivity(new Intent(v.getContext(), SetAlaram.class));
                        break;
                    case 1:
                       // call SilientOffOn activity
                        v.getContext().startActivity(new Intent(v.getContext(), SilientOffOn.class));
                        break;
                    case 2:
                        //call MakeCall activity
                        v.getContext().startActivity(new Intent(v.getContext(), MakeCall.class));
                        break;
                    case 3:
                        // call NavigationActivity activity
                        v.getContext().startActivity(new Intent(v.getContext(), NavigateApp.class));
                        break;
                    case 5:
                        // call SendSMS activity
                        v.getContext().startActivity(new Intent(v.getContext(), WifiOff.class));
                        break;
                    case 7:
                        // call SendContact activity
                        v.getContext().startActivity(new Intent(v.getContext(), BrightnessLowHigh.class));
                        break;
                    case 4:
                        // call BrightnessLowHigh activity
                        v.getContext().startActivity(new Intent(v.getContext(), SendSMS.class));
                        break;
                    case 6:
                       //  call LocationAddress activity
                        v.getContext().startActivity(new Intent(v.getContext(), LocationAddressMe.class));
                        break;
                    case 8:
                        // call PDFActivity activity
                        v.getContext().startActivity(new Intent(v.getContext(), PdfViewerActivity.class));
                        break;
                    case 9:
                        // call PDFActivity activity
                        v.getContext().startActivity(new Intent(v.getContext(), ReadMessage.class));
                        break;
                    case 10:
                        // call SocialMedial activity
                        v.getContext().startActivity(new Intent(v.getContext(), SocialMedial.class));
                        break;
                    case 11:
                        // call SocialMedial activity
                        v.getContext().startActivity(new Intent(v.getContext(), ShareContact.class));
                        break;
                    default:
                        break;
                }
                /*if (position==0) {
                    // Navigate to NFCActivity class.
                    v.getContext().startActivity(new Intent(v.getContext(), NFCActivity.class));
                }*/
                
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImage;
        TextView cardText;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.card_image);
            cardText = itemView.findViewById(R.id.card_text);
        }
    }

}
