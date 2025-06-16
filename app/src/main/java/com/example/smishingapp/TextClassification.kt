package com.example.smishingapp
//
//import android.content.Context
//import androidx.compose.ui.Modifier
//import com.example.smishingapp.ml.Generated1
//import org.json.JSONObject
//import org.tensorflow.lite.DataType
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
//import java.io.IOException
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import java.nio.FloatBuffer
//
//class Classifier(context: Context, jsonFilename: String , inputMaxLen : Int ) {
//
//    private var context : Context? = context
//
//    // Filename for the exported vocab ( .json )
//    private var filename : String? = jsonFilename
//
//    // Max length of the input sequence for the given model.
//    private var maxlen : Int = inputMaxLen
//
//    private var vocabData : HashMap< String , Int >? = null
//
//    // Load the contents of the vocab ( see assets/word_dict.json )
//    private fun loadJSONFromAsset(filename : String? ): String? {
//        val json: String?
//        try {
//            val inputStream = filename?.let { context!!.assets.open(it) }
//            val size = inputStream?.available()
//            val buffer = size?.let { ByteArray(it) }
//            inputStream?.read(buffer)
//            inputStream?.close()
//            json = buffer?.let { String(it) }
//        }
//        catch (ex: IOException) {
//            ex.printStackTrace()
//            return null
//        }
//        return json
//    }
//
//    fun processVocab() {
//        loadVocab(loadJSONFromAsset( filename )!! )
//    }
//
//    // Tokenize the given sentence
//    fun tokenize ( message : String ): IntArray {
//        val parts : List<String> = message.split(" " )
//        val tokenizedMessage = ArrayList<Int>()
//        for ( part in parts ) {
//            if (part.trim() != ""){
//                val index: Int? = if ( vocabData!![part] == null ) {
//                    1
//                } else{
//                    vocabData!![part]
//                }
//                tokenizedMessage.add( index!! )
//            }
//        }
//        return tokenizedMessage.toIntArray()
//    }
//
//    // Pad the given sequence to maxlen with zeros.
//    fun padSequence ( sequence : IntArray ) : IntArray {
//        val maxlen = this.maxlen
//        return if ( sequence.size > maxlen ) {
//            sequence.sliceArray(0 until maxlen)
//        } else if ( sequence.size < maxlen ) {
//            val array = ArrayList<Int>()
//            array.addAll( sequence.asList() )
//            for ( i in array.size until maxlen ){
//                array.add(0)
//            }
//            array.toIntArray()
//        } else{
//            sequence
//        }
//    }
//
//    fun modelClassification(paddedMessage: IntArray): FloatArray{
//        var model = context?.let { Generated1.newInstance(it) }
//        // Creates inputs for reference.
//        val buffer = ByteBuffer.allocateDirect(paddedMessage.size * 4).apply {
//            for (token in paddedMessage) {
//                putFloat(token.toFloat())
//            }
//            rewind()
//        }
//        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 150), DataType.FLOAT32)
//        inputFeature0.loadBuffer(buffer)
//
//    // Runs model inference and gets result.
//        val outputs = model?.process(inputFeature0)
//        val outputFeature0 = outputs?.outputFeature0AsTensorBuffer
//
//    // Releases model resources if no longer used.
//        model?.close()
//        if (outputFeature0 != null) {
//            return outputFeature0.floatArray
//        }
//        return floatArrayOf(0.0f)
//    }
//
//    private fun loadVocab(json : String )  {
//            val jsonObject = JSONObject( json )
//            val iterator : Iterator<String> = jsonObject.keys()
//            val data = HashMap< String , Int >()
//            while ( iterator.hasNext() ) {
//                val key = iterator.next()
//                data[key] = jsonObject.get( key ) as Int
//            }
//            vocabData = data
//    }
//
//}