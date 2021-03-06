# OMO Smart Home

Projekt simulace chytré domácnosti. Simuluje se život lidí a jejich zvířat v domě, jejich vzájemná interakce a používání zařízení, vozidel a sportovního vybavení. Také se simulují změny počasí, práce senzorů pro čtení dat. V případě kritických odečtů sensorů jednají obyvatelé domu a zařízení podle předem stanoveného plánu. Pro získání kompletních informací o průběhu simulace se používá logování a příprava 4 typů reportů na konci simulace. Reporty jsou uloženy v textových souborech s následujícím názvem {číslo konfigurace (1 nebo 2)}+{typ reportu}+{data}+{čas}. Konfigurační soubory, včetně příruček pro údržbu zařízení, jsou uloženy ve formátu json. Takové konfigurace 2. Návod k použití zařízení je pro ně společný.

# Základní instrukce

Před zahájením simulace se vyplatí zkontrolovat vstupní parametry. Konkrétně, že číslo konfigurace je 1 nebo 2. Také, že počáteční datum a čas jsou ve formátu "yyyy-MM-ddTHH:mm:ss". Doba trvání simulace je uvedena jako poslední vstupní parametr.

# Funkční požadavky

<details><summary>Funkční požadavky</summary>

**F1.** [+]	Entity se kterými pracujeme je dům, okno (+ venkovní žaluzie), patro v domu, senzor, zařízení (=spotřebič), osoba, auto, kolo, domácí zvíře jiného než hospodářského typu, plus libovolné další entity.

[-] _Okna nemají žaluzie_


**F2.** 
Jednotlivá zařízení v domu mají API na ovládání. Zařízení mají stav, který lze měnit pomocí API na jeho ovládání. Akce z API jsou použitelné podle stavu zařízení. Vybraná zařízení mohou mít i obsah - lednice má jídlo, CD přehrávač má CD.


**F3.** 
Spotřebiče mají svojí spotřebu v aktivním stavu, idle stavu, vypnutém stavu.


**F4.**
Jednotlivá zařízení mají API na sběr dat o tomto zařízení. O zařízeních sbíráme data jako spotřeba elektřiny, plynu, vody a funkčnost (klesá lineárně s časem).


**F5.**	
Jednotlivé osoby a zvířata mohou provádět aktivity(akce), které mají nějaký efekt na zařízení nebo jinou osobu. 


**F6.**
Jednotlivá zařízení a osoby se v každém okamžiku vyskytují v jedné místnosti (pokud nesportují) a náhodně generují eventy (eventem může být důležitá informace a nebo alert).

! Lidé, kteří nejsou malé děti, mohou dům opustit libovolně. Člověk může vyjít z domu i na procházku se psem._


**F7.**	Eventy jsou přebírány a odbavovány vhodnou osobou (osobami) nebo zařízením (zařízeními).


**F8.**	Vygenerování reportů:

 ○	HouseConfigurationReport: veškerá konfigurační data domu zachovávající hieararchii - dům -> patro -> místnost -> okno -> žaluzie atd Plus jací jsou obyvatelé domu.
 
 ○	EventReport: report eventů, kde grupujeme eventy podle typu, zdroje eventů a jejich cíle (jaká entita event odbavila)

○	ActivityAndUsageReport: Report akcí (aktivit) jednotlivých osob a zvířat, kolikrát které osoby použily které zařízení.

○	ConsumptionReport: Kolik jednotlivé spotřebiče spotřebovaly elektřiny, plynu, vody. Včetně finančního vyčíslení.


**F9.** Při rozbití zařízení musí obyvatel domu prozkoumat dokumentaci k zařízení - najít záruční list, projít manuál na opravu a provést nápravnou akcí (např. Oprava svépomocí, koupě nového atd.). Manuály zabírají mnoho místa a trvá dlouho než je najdete


**F10.** Rodina je aktivní a volný čas tráví zhruba v poměru (50% používání spotřebičů v domě a 50% sport kdy používá sportovní náčiní kolo nebo lyže). Když není volné zařízení nebo sportovní náčiní, tak osoba čeká.

! _Generátor ne vždy rovnoměrně generuje události pro použití zařízení a transportu._

</details>

# Nefunkční požadavky

<details><summary>Nefunkční požadavky</summary>


- **N1.** Není požadována autentizace ani autorizace.


- **N2.** Aplikace může běžet pouze v jedné JVM.


- **N3.** Aplikaci pište tak, aby byly dobře schované metody a proměnné, které nemají být dostupné ostatním třídám. Vygenerovný javadoc by měl mít co nejméně public metod a proměnných.


- **N4.**	Reporty jsou generovány do textového souboru.

- **N4.** Konfigurace domu, zařízení a obyvatel domu může být nahrávána přímo z třídy nebo externího souboru (preferován je json).


</details>

# Design patterny

<details><summary>patterny</summary>

+ **P1.** Visitor 

Používá se při vytváření rerortů (HouseConfigurationReport, EventReport, ConsumptionReport). Umožňuje přidat operaci pro získání všech dat o objektů bez provádění změn ve stávající struktuře objektů. Visitor tak může objekt navštívit a provést požadovanou akci.


+  **P2.** State

Mění výkon zařízení v závislosti na stavu. Takže ve vypnutém(OFF) a blokovaném stavu(BLOCK) se rovná 0, v aktivním stavu(ACTIVE) 100 % a v klidovém stavu(IDLE) 50 % výkonu v aktivním stavu.


+ **P3.** Proxy + Lazy Initialization

Umožněte implementovat následující. Pokud se zařízení porouchá, musí si člověk stáhnout a otevřít návod k opravě, ale protože není potřeba stahovat dokument předem, stáhne se až před opravou zařízení.


+ **P4.** Observer

Umožňuje zařízením, lidem a domácím zvířatům poslouchat změny senzorů. Při kritických měřeních vlhkosti, teploty, kouře atd. zareagují a pokusí se vrátit naměřené hodnoty do normálního stavu.


+ **P5.** Factory

Používají se 2 továrny, první na výrobu zařízení, další na výrobu senzorů. Továrny umožňují oddělit kód pro vytváření objektů od zbytku kódu, který je používá.


+ **P6.** Facade

Prostřednictvím něj se ovládají zařízení (zapínání, používání, blokování, vypínání, oprava atd.). Proto lze volat jednu metodu k opravě zařízení nebo k provedení jiné operace na zařízení.


+ **P7.** Builder

Používá se k postupnému vytváření objektů, jako je zařízení, osoba, zvíře, vozidlo.


+ **P8.** Singleton

Zaručuje, že bude existovat pouze jeden objekt House.

</details>


# Zkušenosti získané během SP

<details><summary>zkušenosti</summary>

Nejprve jsem začala vytvářením diagramů, poté jsem vytvářela modely. Jak projekt rostl, modely se několikrát změnily. Implementace všech vzorů kromě Observeru netrvala dlouho. Ze všech patternů se mi ukázal jako nejobtížnější na pochopení. Celkově jsem spokojená s tím, jak jsem vzory naimplementovala. Když jsem začala vytvářet reporty, uvědomila jsem si, že jsem nevytvořila příliš vhodnou strukturu. Totiž, ponechat samostatné typy zařízení a senzorů, každý ve svém vlastním listu, se mi nezdá nejlepší nápad. Ale bohužel nebyl čas vše změnit. Nejdelší částí práce bylo vytvoření generátoru eventů. Pro mě to byla nejen nejdelší část, ale i nejtěžší. Stále to nefunguje tak, jak bych chtěla. Fronty nejsou správně vyčištěny při použití transportu. S největší pravděpodobností je to proto, že jsem byla časově omezená a velmi rychlá. Celkově se mi práce na projektu líbila. Hlavní věc, kterou jsem se pro sebe naučila, je, že používání vzorů jako konceptů k řešení konkrétního problému šetří čas a snižuje pravděpodobnost neočekávaných chyb.

</details>


# Contacts

Anna Kachmasheva 

   [annakachmasheva@gmail.com]()

#� �s�m�a�r�t�-�H�o�m�e�_�s�i�m�u�l�a�t�i�o�n�
�
�
