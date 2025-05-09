# Custom Fonts Setup Guide

This guide explains how to download and add the custom fonts (Nunito and Public Sans) to the application.

## Required Fonts

The application uses two custom fonts:

1. **Nunito** - For body text and regular content
2. **Public Sans** - For headings and titles

## Download Instructions

### Option 1: Download from Google Fonts (Recommended)

1. **Nunito Font:**
   - Go to [Nunito on Google Fonts](https://fonts.google.com/specimen/Nunito)
   - Click "Download family"
   - Unzip the downloaded file

2. **Public Sans Font:**
   - Go to [Public Sans on Google Fonts](https://fonts.google.com/specimen/Public+Sans)
   - Click "Download family"
   - Unzip the downloaded file

### Option 2: Use Pre-downloaded Fonts

If you have access to the project assets, the font files may be available in the `fonts` directory.

## Adding Fonts to the Project

1. Make sure the following fonts are included:
   - nunito_regular.ttf
   - nunito_bold.ttf
   - nunito_light.ttf
   - nunito_semibold.ttf
   - publicsans_regular.ttf
   - publicsans_bold.ttf
   - publicsans_light.ttf
   - publicsans_semibold.ttf

2. Copy these font files into the `app/src/main/res/font/` directory.

3. The app is already set up to use these fonts in the Typography class at:
   `app/src/main/java/com/example/ndejjenews/ui/theme/Type.kt`

## Verifying Fonts

To verify that the fonts are correctly set up:

1. Run the app
2. Check that different text elements are displayed with the correct fonts
3. Headings should use Public Sans
4. Body text should use Nunito 