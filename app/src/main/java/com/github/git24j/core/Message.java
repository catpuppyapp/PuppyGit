package com.github.git24j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;

public class Message {
    /**
     * int git_message_prettify(git_buf *out, const char *message, int strip_comments, char
     * comment_char);
     */
    static native int jniPrettify(Buf out, String message, int stripComments, char commentChar);

    /** void git_message_trailer_array_free(git_message_trailer_array *arr); */
    static native void jniTrailerArrayFree(long arr);

    /** size_t count */
    static native int jniTrailerArrayGetCount(long trailerArrayPtr);

    /** git_message_trailer *trailers */
    static native long jniTrailerArrayGetTrailer(long trailerArrayPtr, int idx);

    /** char * _trailer_block */
    static native String jniTrailerArrayGetTrailerBlock(long trailerArrayPtr);

    /** const char *key */
    static native String jniTrailerGetKey(long trailerPtr);

    /** const char *value */
    static native String jniTrailerGetValue(long trailerPtr);

    /** int git_message_trailers(git_message_trailer_array *arr, const char *message); */
    static native int jniTrailers(AtomicLong outArr, String message);

    @Nonnull
    public static TrailerArray trailers(@Nonnull String message) {
        AtomicLong outArr = new AtomicLong();
        Error.throwIfNeeded(jniTrailers(outArr, message));
        long trailerArrPtr = outArr.get();
        int n = jniTrailerArrayGetCount(trailerArrPtr);
        List<Trailer> trailers = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            long trailerPtr = jniTrailerArrayGetTrailer(trailerArrPtr, i);
            String key = jniTrailerGetKey(trailerPtr);
            String val = jniTrailerGetValue(trailerPtr);
            trailers.add(new Trailer(key, val));
        }
        String trailerBlock = jniTrailerArrayGetTrailerBlock(trailerArrPtr);
        jniTrailerArrayFree(trailerArrPtr);
        return new TrailerArray(trailers, trailerBlock);
    }

    /**
     * Clean up excess whitespace and make sure there is a trailing newline in the message.
     *
     * <p>Optionally, it can remove lines which start with the comment character.
     *
     * @return the cleaned up message.
     * @param message The message to be prettified.
     * @param stripComments true to remove comment lines, false to leave them in.
     * @param commentChar Comment character. Lines starting with this character are considered to be
     *     comments and removed if `strip_comments` is non-zero.
     * @throws GitException git2 exceptions
     */
    public static String prettify(
            @Nonnull String message, boolean stripComments, char commentChar) {
        Buf out = new Buf();
        int e = jniPrettify(out, message, stripComments ? 1 : 0, commentChar);
        Error.throwIfNeeded(e);
        return out.getString().orElse("");
    }

    /** Represents a single git message trailer. */
    public static class Trailer {
        private final String _key;
        private final String _value;

        public Trailer(String key, String value) {
            _key = key;
            _value = value;
        }

        public String getKey() {
            return _key;
        }

        public String getValue() {
            return _value;
        }
    }

    /** Represents an array of git message trailers. */
    public static class TrailerArray {
        private final List<Trailer> _trailers;
        private final String _trailerBlock;

        public TrailerArray(List<Trailer> trailers, String trailerBlock) {
            _trailers = trailers;
            _trailerBlock = trailerBlock;
        }

        public List<Trailer> getTrailers() {
            return _trailers;
        }

        String getTrailerBlock() {
            return _trailerBlock;
        }
    }
}
