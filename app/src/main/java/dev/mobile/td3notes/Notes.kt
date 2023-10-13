package dev.mobile.td3notes

class Notes(private val notes: MutableList<Note> = mutableListOf(),
            private var cursor: Int = -1) {

    fun ajouter(n: Note) {
        notes.add(n)
        cursor = notes.size - 1
    }

    fun supprimer() {
        if (!estVide()) {
            notes.removeAt(cursor)
            if (!notes.isEmpty()) cursor = cursor % notes.size
        }
    }

    fun suivante(): Note? {
        if (estVide()) return null
        cursor = (cursor + 1) % notes.size
        return notes[cursor]
    }

    fun courante(): Note? {
        return if (estVide()) null else notes[cursor]
    }

    fun precedente(): Note? {
        if (estVide()) return null
        cursor = (cursor - 1 + notes.size) % notes.size
        return notes[cursor]
    }

    fun estVide(): Boolean {
        return notes.isEmpty()
    }

    companion object {
        fun init(): Notes {
            val n = Notes()
            n.ajouter(Note("Remm", 2.5, "Peut mieux faire..."))
            n.ajouter(Note("Berdjugin", 5.0, "Le meilleur !"))
            n.ajouter(Note("Lanoix", 4.5, "Super !"))
            return n
        }
    }
}