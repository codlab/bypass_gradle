# Markdown and Android

This library intends to provide to your app the most complete Markdown syntax natively.

# Bypass

This library is based on Bypass which is directly embedded in its source. Note that you still can provide your own version of Bypass.

# Image support

The library facilitate the Image loading via two different syntaxes which offer http/https loading and images from drawables :

>  ![Legend](url)
>  ![Legend](drawable_name)

NOTE : currently, the legend is not used to render the image - fixes for assets loading is on-going

# array support

The library support one array syntax to improve your document rendered.
Header can be rendered by adding a second 'describing line'.

i.e. :
> --------  --------
> Column 1  Column 2
> --------  --------
> Row 1     >> val 1
> 
>           Row 2
> 
> Row 3     !empty
> --------  --------


# Customisation

Some attributes can be used to personnalized the rending :

md_array_header_color : the background color used in the arrays' header
md_array_body_color : the background color used in the arrays' body
md_text_color : the overall default text color
md_array_spacing : the arrays' border width
md_cell_padding : the text padding in the arrays

# Integration

Currently the app must be integrated as submodule into your apps. Once done, simply use :


>    <eu.codlab.markdown.MarkdownView
>      xmlns:md="http://schemas.android.com/apk/res-auto"
>      android:id="@+id/markdown"
>      android:layout_width="match_parent"
>      android:layout_height="match_parent"
>      md:md_array_body_color="@color/red"
>      md:md_array_spacing="@dimen/fragment_margin" />
