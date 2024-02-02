<h1 align="center">
  <img src="logo_medicalink.png" alt="Medicalink" height="200">
  <br />
</h1>

<p align="center"><b>This project is licensed under the EUPL 1.2. For more details, see the <a href="LICENSE.md">LICENSE</a> file.</b></p>
<p align="center"><i>"An Application that allows users to scan prescriptions, medications, and sends notifications for intakes"</i></p>


## Authors
### Main Squad
* Nicolas Chusseau ([NicolasChusseau](https://github.com/NicolasChusseau))
* Pacôme Cailleteau ([PacomeCailleteau](https://github.com/PacomeCailleteau))
* Lilian Delhommeau (E211556C) ([Redly0n](https://github.com/Redly0n))
### Maintenance Squad
* Pacôme Cailleteau ([PacomeCailleteau](https://github.com/PacomeCailleteau))
* Lucas Bigeard ([LucWaw](https://github.com/LucWaw))
* Swan Gonzales ([SwanGonzales](https://github.com/SwanGon))
* Arthur Osselin ([Arthur Osselin]())
## Related Git repository
* Main Android application : [Medicalink](https://gitlab.univ-nantes.fr/E211556C/sae_medicalink#----)
* Maintenance application : [MedicalinkMaintenance](https://github.com/LucWaw/MedicalinkMaintenance)
* Artificial Intelligence Generator (Named Entity Recognition) : [MedicAppAI](https://github.com/anrouxel/MedicAppAI#----)
* Named Entity Recognition AI model for prescriptions (usable as a sub-module) : [MedicAppAssets](https://gitlab.univ-nantes.fr/E213726L/MedicAppAssets.git)

## Special thanks
* Antonin Rouxel ([anrouxel](https://github.com/anrouxel)) for AI Generator and Named Entity Recognition AI model for prescriptions.


## Usage

In order to load assets from the model, use the following commands :

On linux:

```curl -s https://packagecloud.io/install/repositories/github/git-lfs/script.deb.sh | sudo bash```

```sudo apt install git-lfs```


In assets repository:

```git submodule update --init```


Pull model :

```git submodule foreach git lfs pull```
