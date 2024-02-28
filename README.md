<h1 align="center">
  <img src="logo_medicalink.png" alt="Medicalink" height="200">
  <br />
</h1>

<p align="center"><b>This project is licensed under the EUPL 1.2. For more details, see the <a href="LICENSE.md">LICENSE</a> file.</b></p>
<p align="center"><i>"An Application that allows users to scan prescriptions, medications, and sends notifications for intakes"</i></p>

## Forked from [Medicalink](https://github.com/PacomeCailleteau/Medicalink#----)
This is a fork of the original Medicalink project, which was developed by a team of students from the University of Nantes.  
For our last project, we had to take a project and improve it.  
We had to add many new features, fix bugs, and improve the overall code quality of the application.  
To improve the code quality, we've added SonarCloud to the project, and we've fixed many issues that were found by SonarCloud. 
Moreover, we've changed the architecture of the project to make it more maintainable and scalable.  
For exemple, we have changed all our buddle during the creation of a treatment to a ViewModel. Thanks to that, it's easier to manage the data and to add new features.    
We've fixed many bugs and improved many existing features such as the notifications, the ORC.    
We've added new features such as the possibility to check and change our informations, the possibility to add our practician getting all his informations.  

## Contributors
* Nicolas CHUSSEAU ([NicolasChusseau](https://github.com/NicolasChusseau))
* Mathieu BERGERON ([Mthieu44](https://github.com/Mthieu44))
* Adrien SÉAC'H ([Adrien SEAC'H](https://github.com/Ad2rien5))
* Mattéo PACINI ([Senko999](https://github.com/Senko999))

## Related Git repository
* Android application: [MedicaZelda](https://github.com/Mthieu44/MedicaZelda#----)
* Artificial Intelligence Generator (Named Entity Recognition): [MedicAppAI](https://github.com/anrouxel/MedicAppAI#----)
* Named Entity Recognition AI model for prescriptions (usable as a sub-module): [MedicAppAssets](https://gitlab.univ-nantes.fr/E213726L/MedicAppAssets.git)
* Practicians API: [MedicaZeldaAPI](https://github.com/NicolasChusseau/MedicaZelda_API/)
* Medication Interactions PDF scraper: [PDFScaper](https://github.com/Mthieu44/interactionMedicamentPdfScraper)

## Special thanks
* Antonin Rouxel ([anrouxel](https://github.com/anrouxel)) for AI Generator and Named Entity Recognition AI model for prescriptions.


## Usage

In order to load assets from the model, use the following commands:

On linux:

```
curl -s https://packagecloud.io/install/repositories/github/git-lfs/script.deb.sh | sudo bash
```

```
sudo apt install git-lfs
```


In assets repository:

```
git submodule update --init  
git submodule foreach git lfs pull
```


