The iteration 1 cropped wireframes have an offset which is equal to the amount cut from the original 
wireframe. This offset is used to translate the coordinates of Nodes from the CSV files to 
applicable coordinates which can be used to draw the Node locations on the cropped wireframe. 

Applicable wireframes: 2-ICONS-CROPPED.png, 2-NO-ICONS-CROPPED.png

Size of cropped wireframes: 1380px x 776px
X-offset: 2900px
Y-offset: 1300px

Original: 5000px x 2774px
X-offset: 0px
Y-offset: 0px

To draw a node at (xCoord,yCoord) with new image size displaySizeX x displaySizeY:
xCenter = (xCoord - X-offset) * (displaySizeX / 1380)
yCenter = (yCoord - Y_offset) * (displaySizeY / 776)