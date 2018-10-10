package net.openhft.chronicle.wire;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.ref.BinaryIntReference;
import net.openhft.chronicle.bytes.ref.BinaryLongReference;
import net.openhft.chronicle.bytes.ref.LongReference;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.values.IntValue;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Created by Rob Austin
 */
public class BinaryLongValueBitSet extends AbstractLongValueBitSet {

    /**
     * Creates a bit set using words as the internal representation.
     * The last word (if there is one) must be non-zero.
     *
     * @param words
     */
    public BinaryLongValueBitSet(final LongReference[] words) {
        super(words);
    }

    @Override
    public void readMarshallable(@NotNull final WireIn wire) throws IORuntimeException {
        int numberOfLongValues = wire.read("numberOfLongValues").int32();
        BinaryIntReference wordsInUse = new BinaryIntReference();

        Bytes<?> bytes = wire.bytes();
        wordsInUse.bytesStore(Objects.requireNonNull(bytes.bytesStore()), bytes.readPosition(), 4);
        this.wordsInUse = wordsInUse;
        bytes.readSkip(4);

        words = new BinaryLongReference[numberOfLongValues];
        for (int i = 0; i < numberOfLongValues; i++) {

            // todo improve this so that it works with text wire
            BinaryLongReference ref = new BinaryLongReference();
            ref.bytesStore(Objects.requireNonNull(bytes.bytesStore()), bytes.readPosition(), 8);
            words[i] = ref;
            bytes.readSkip(8);

        }
    }

    @Override
    public void writeMarshallable(@NotNull final WireOut wire) {
        wire.write("numberOfLongValues").int32(words.length);
        //wordsInUse
        wire.bytes().writeSkip(4);

        // because this is a LongValue bit set the "words" are bound on the call to net.openhft.chronicle.wire.LongValueBitSet.readMarshallable
        wire.bytes().writeSkip(words.length * 8);
    }

    @NotNull
    protected IntValue newIntValue() {
        return new BinaryIntReference();
    }

}
