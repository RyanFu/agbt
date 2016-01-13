package com.github.yanglw.agbt.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.BufferedImage

/**
 * <p>
 * 二维码工具类。
 * </p>
 * Created by yanglw on 2015-12-15.
 */
class QRCodeUtil {
    public static final String QR_CODE_FILE_FORMAT_NAME = 'png'
    public static final int QR_CODE_FILE_IMAGE_SIZE = 250

    /**
     * 生成二维码至文件。
     * @param qrFile 目标文件。
     * @param qrCodeText 二维码内容。
     * @param size 二维码图片尺寸。
     * @param fileType 图片类型。
     */
    static void createQRImage(File qrFile, String qrCodeText, int size, String fileType) {
        Hashtable hintMap = new Hashtable()
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
        QRCodeWriter qrCodeWriter = new QRCodeWriter()
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText,
                                                   BarcodeFormat.QR_CODE,
                                                   size,
                                                   size,
                                                   hintMap)

        int matrixWidth = byteMatrix.getWidth()
        BufferedImage image = new BufferedImage(matrixWidth,
                                                matrixWidth,
                                                BufferedImage.TYPE_INT_RGB)
        image.createGraphics()

        Graphics2D graphics = (Graphics2D) image.getGraphics()
        graphics.setColor(Color.WHITE)
        graphics.fillRect(0, 0, matrixWidth, matrixWidth)
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK)

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1)
                }
            }
        }
        ImageIO.write(image, fileType, qrFile)
    }
}
