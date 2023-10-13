package dev.mobile.td3notes

import android.os.Parcel
import android.os.Parcelable

class Note(var nom: String?, var note: Double, var commentaire: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString()
    )

    override fun toString(): String {
        return "Note{" +
                "nom='" + nom + '\'' +
                ", note=" + note +
                ", commentaire='" + commentaire + '\'' +
                '}'
    }

    init {
        require(!(note < 0 || note > NOTE_MAX)) { "Illegal note: $note" }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nom)
        parcel.writeDouble(note)
        parcel.writeString(commentaire)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object{
        const val NOTE_MAX = 5.0
        val FAKE_NOTE = Note("", 0.0, "")

        @JvmField
        val CREATOR = object : Parcelable.Creator<Note> {
            override fun createFromParcel(parcel: Parcel): Note {
                return Note(parcel)
            }

            override fun newArray(size: Int): Array<Note?> {
                return arrayOfNulls(size)
            }
        }
    }
}
