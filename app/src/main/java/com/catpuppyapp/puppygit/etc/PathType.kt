package com.catpuppyapp.puppygit.etc

import com.catpuppyapp.puppygit.utils.FsUtils.absolutePathPrefix
import com.catpuppyapp.puppygit.utils.FsUtils.contentUriPathPrefix
import com.catpuppyapp.puppygit.utils.FsUtils.fileUriPathPrefix

enum class PathType {
   INVALID,  // empty or blank or other invalid path
   CONTENT_URI, // starts with "content://"
   FILE_URI,  // starts with "file://", very rare use now in android
   ABSOLUTE  // stars with "/"

   ;

   companion object {
      fun getType(path:String): PathType {
         return if(path.startsWith(absolutePathPrefix)) {
            ABSOLUTE
         }else if(path.startsWith(contentUriPathPrefix)) {
            CONTENT_URI
         }else if(path.startsWith(fileUriPathPrefix)) {
            FILE_URI
         }else {
            INVALID
         }
      }
   }
}

