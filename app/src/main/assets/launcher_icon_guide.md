# Custom Launcher Icon Setup Guide

This guide explains how to create and set up a custom launcher icon for the Ndejje News application.

## Using Android Studio's Image Asset Studio (Recommended)

1. **Open Image Asset Studio:**
   - Right-click on the `res` folder in your project
   - Select `New > Image Asset`

2. **Configure the Launcher Icon:**
   - In the Asset Type field, select "Launcher Icons (Adaptive and Legacy)"
   - In the Name field, enter `ic_launcher`
   - In the Foreground Layer tab:
      - Select "Image" as the source asset type
      - Click on the "..." button to select an image
      - Choose a high-resolution PNG or SVG image representing Ndejje News (e.g., a newspaper or university logo)
      - Adjust the scaling options as needed
   - In the Background Layer tab:
      - Choose a solid color (preferably NdejjeBlue or NdejjeGold from our color scheme)
      - Or select an image for a more complex background
   - Preview the icon to ensure it looks good in different shapes

3. **Generate the Icons:**
   - Click "Next" to see which resources will be created
   - Click "Finish" to generate the icon files

This will create all necessary icon resources in the `mipmap` folders, replacing the default Android icon.

## Alternative Method: Manual Icon Creation

If you have pre-designed icons or prefer more control:

1. **Prepare Your Icon Files:**
   - Create PNG files in multiple resolutions:
     - mdpi: 48x48 px
     - hdpi: 72x72 px
     - xhdpi: 96x96 px
     - xxhdpi: 144x144 px
     - xxxhdpi: 192x192 px
   - For adaptive icons (Android 8.0+), prepare:
     - A foreground image (with transparency)
     - A background color or image

2. **Replace Existing Icons:**
   - Navigate to each mipmap folder (mipmap-mdpi, mipmap-hdpi, etc.)
   - Replace the existing ic_launcher files with your custom icons
   - For adaptive icons, update the ic_launcher_foreground and ic_launcher_background resources

3. **Update the Icon XML (for Adaptive Icons):**
   - Open `mipmap-anydpi-v26/ic_launcher.xml` and ensure it references your new resources

## Verifying the Icon

To verify that the icon has been correctly set up:

1. Run the app on a device or emulator
2. Check the app's icon on the device's home screen
3. The icon should display your custom design instead of the default Android icon 