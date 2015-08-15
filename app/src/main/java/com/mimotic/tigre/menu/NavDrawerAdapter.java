package com.mimotic.tigre.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mimotic.tigre.R;
import com.squareup.picasso.Picasso;


public class NavDrawerAdapter extends ArrayAdapter<NavDrawerItem> {

    private Context context;
    private LayoutInflater inflater;

    public NavDrawerAdapter(Context context, int textViewResourceId, NavDrawerItem[] objects ) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null ;
        NavDrawerItem menuItem = this.getItem(position);
        if ( menuItem.getType() == NavMenuItem.ITEM_TYPE ) {
            view = getItemView(convertView, parent, menuItem);
        }else if(menuItem.getType() == NavMenuHeader.HEADER_TYPE) {
            view = getHeaderView(convertView, parent, menuItem);
        }else {
            view = getSectionView(convertView, parent, menuItem);
        }
        return view ;
    }

    public View getItemView( View convertView, ViewGroup parentView, NavDrawerItem navDrawerItem ) {

        NavMenuItem menuItem = (NavMenuItem) navDrawerItem ;
        NavMenuItemHolder navMenuItemHolder = null;

//        if (convertView == null) {
//        if(menuItem.isDestacado()){
//            convertView = inflater.inflate( R.layout.navdrawer_item_destacado, parentView, false);
//        }else{
            convertView = inflater.inflate( R.layout.navdrawer_item, parentView, false);
//        }

        TextView labelView = (TextView) convertView.findViewById(R.id.navmenuitem_label);
        ImageView iconView = (ImageView) convertView.findViewById(R.id.navmenuitem_icon);

        navMenuItemHolder = new NavMenuItemHolder();
        navMenuItemHolder.labelView = labelView ;
        navMenuItemHolder.iconView = iconView ;

        convertView.setTag(navMenuItemHolder);
//        }

//        if ( navMenuItemHolder == null ) {
//            navMenuItemHolder = (NavMenuItemHolder) convertView.getTag();
//        }

        navMenuItemHolder.labelView.setText(menuItem.getLabel());
        navMenuItemHolder.iconView.setImageResource(menuItem.getIcon());

        return convertView ;
    }

    public View getSectionView(View convertView, ViewGroup parentView,
                               NavDrawerItem navDrawerItem) {

        NavMenuSection menuSection = (NavMenuSection) navDrawerItem ;
        NavMenuSectionHolder navMenuItemHolder = null;

        if (convertView == null) {
            convertView = inflater.inflate( R.layout.navdrawer_section, parentView, false);
            TextView labelView = (TextView) convertView
                    .findViewById( R.id.navmenusection_label );

            navMenuItemHolder = new NavMenuSectionHolder();
            navMenuItemHolder.labelView = labelView ;
            convertView.setTag(navMenuItemHolder);
        }

        if ( navMenuItemHolder == null ) {
            navMenuItemHolder = (NavMenuSectionHolder) convertView.getTag();
        }

        navMenuItemHolder.labelView.setText(menuSection.getLabel());

        return convertView ;
    }


    public View getHeaderView(View convertView, ViewGroup parentView,
                              NavDrawerItem navDrawerItem) {

        NavMenuHeader menuheader = (NavMenuHeader) navDrawerItem ;
        NavMenuHeaderHolder navMenuItemHolder = null;

        if (convertView == null) {
            convertView = inflater.inflate( R.layout.navdrawer_header, parentView, false);
            TextView nameView = (TextView) convertView.findViewById(R.id.header_name);
            ImageView photoView = (ImageView) convertView.findViewById(R.id.header_logo);

            navMenuItemHolder = new NavMenuHeaderHolder();
            navMenuItemHolder.nameView = nameView;
            navMenuItemHolder.photoView = photoView;
            convertView.setTag(navMenuItemHolder);
        }

        if ( navMenuItemHolder == null ) {
            navMenuItemHolder = (NavMenuHeaderHolder) convertView.getTag();
        }

//        Picasso.with(context).load(menuheader.getUrlPhoto()).into(navMenuItemHolder.photoView);
                //.transform(new CircleTransform())

//        navMenuItemHolder.nameView.setText(menuheader.getName());

        return convertView ;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return this.getItem(position).getType();
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).isEnabled();
    }


    private static class NavMenuItemHolder {
        private TextView labelView;
        private ImageView iconView;
    }

    private class NavMenuSectionHolder {
        private TextView labelView;
    }

    private class NavMenuHeaderHolder {
        private TextView nameView;
        private ImageView photoView;
    }
}
