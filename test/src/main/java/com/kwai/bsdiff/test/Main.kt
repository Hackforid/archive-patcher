package com.kwai.bsdiff.test

import com.google.archivepatcher.generator.FileByFileV1DeltaGenerator
import com.google.archivepatcher.shared.DefaultDeflateCompatibilityWindow
import java.io.File
import java.io.FileOutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream


//fun main(args: Array<String>) {
//    Main().run(args)
//}

@Throws(Exception::class)
fun main(args: Array<String>) {
    if (!DefaultDeflateCompatibilityWindow().isCompatible()) {
        System.err.println("zlib not compatible on this system")
        System.exit(-1)
    }
    val oldFile = File(args[0]) // must be a zip archive
    val newFile = File(args[1]) // must be a zip archive
    val compressor = Deflater(9, true) // to compress the patch
    try {
        FileOutputStream(args[2]).use { patchOut ->
            DeflaterOutputStream(patchOut, compressor, 32768)
                .use { compressedPatchOut ->
                    FileByFileV1DeltaGenerator().generateDelta(
                        oldFile,
                        newFile,
                        compressedPatchOut
                    )
                    compressedPatchOut.finish()
                    compressedPatchOut.flush()
                }
        }
    } finally {
        compressor.end()
    }
}