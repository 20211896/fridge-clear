package com.example.t3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.t3.R;
import com.example.t3.model.KamisProduct;
import com.example.t3.ui.ProductDetailActivity; // 추가 ⭐
import com.example.t3.utils.CustomToast;        // 변경된 임포트

import java.util.ArrayList;
import java.util.List;

public class KamisProductAdapter extends RecyclerView.Adapter<KamisProductAdapter.KamisProductViewHolder> {

    private List<KamisProduct> products = new ArrayList<>();
    private OnKamisProductClickListener listener;

    public interface OnKamisProductClickListener {
        void onAddToCartClick(KamisProduct product);
    }

    public void setOnProductClickListener(OnKamisProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<KamisProduct> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KamisProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kamis_product, parent, false);
        return new KamisProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KamisProductViewHolder holder, int position) {
        KamisProduct product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class KamisProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView txtName, txtPrice, txtCategory, txtUnit, txtGrade;
        Button btnAddCart;

        public KamisProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.img_kamis_category);
            txtName = itemView.findViewById(R.id.txt_kamis_name);
            txtPrice = itemView.findViewById(R.id.txt_kamis_price);
            txtCategory = itemView.findViewById(R.id.txt_kamis_category);
            txtUnit = itemView.findViewById(R.id.txt_kamis_unit);
            txtGrade = itemView.findViewById(R.id.txt_kamis_grade);
            btnAddCart = itemView.findViewById(R.id.btn_kamis_add_cart);
        }

        public void bind(KamisProduct product) {
            txtName.setText(product.getFullName());
            txtPrice.setText(product.getFormattedPrice());
            txtCategory.setText(product.getCategoryName());
            txtUnit.setText(product.getUnitName());
            txtGrade.setText(product.getRankName());

            // 실제 농산물 이미지 로드
            loadProductImage(product);

            // 🆕 카드 전체 클릭 리스너 추가 - 상품 상세 페이지로 이동
            itemView.setOnClickListener(v -> {
                ProductDetailActivity.start(itemView.getContext(), product);
            });

            // 장바구니 추가 버튼 클릭
            btnAddCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(product);
                } else {
                    CustomToast.show(itemView.getContext(),
                            product.getFullName() + " 장바구니에 추가!");
                }
            });
        }

        private void loadProductImage(KamisProduct product) {
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_menu_gallery)
                        .error(R.drawable.ic_menu_gallery)
                        .centerCrop()
                        .into(imgCategory);
                imgCategory.setBackgroundColor(0x00000000);
            } else {
                setCategoryIcon(product.getCategoryName());
            }
        }

        private void setCategoryIcon(String category) {
            switch (category) {
                case "식량작물":
                    imgCategory.setBackgroundColor(0xFFFFEB3B);
                    break;
                case "채소류":
                    imgCategory.setBackgroundColor(0xFF4CAF50);
                    break;
                case "과일류":
                    imgCategory.setBackgroundColor(0xFFFF9800);
                    break;
                case "축산물":
                    imgCategory.setBackgroundColor(0xFFF44336);
                    break;
                case "수산물":
                    imgCategory.setBackgroundColor(0xFF2196F3);
                    break;
                default:
                    imgCategory.setBackgroundColor(0xFF9E9E9E);
                    break;
            }
            imgCategory.setImageResource(R.drawable.ic_menu_gallery);
            imgCategory.setScaleType(ImageView.ScaleType.CENTER);
        }
    }
}
