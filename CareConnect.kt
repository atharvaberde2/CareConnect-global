import java.awt.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class CareConnectApp : JFrame() {

    private val cardLayout = CardLayout()
    private val mainPanel = JPanel(cardLayout)

    private lateinit var nameField: JTextField
    private lateinit var addressField: JTextField
    private lateinit var zipcodeField: JTextField
    private lateinit var phoneField: JTextField
    private lateinit var countryCombo: JComboBox<Locale>
    private lateinit var caretakerTypeBox: JComboBox<String>

    init {
        title = "CareConnect"
        defaultCloseOperation = EXIT_ON_CLOSE
        extendedState = JFrame.MAXIMIZED_BOTH

        mainPanel.layout = cardLayout
        mainPanel.border = EmptyBorder(40, 60, 40, 60)
        mainPanel.background = Color(250, 250, 250)

        mainPanel.add(createLandingPage(), "Landing")
        mainPanel.add(createFormPage(), "Form")
        mainPanel.add(createConfirmationPage(), "Confirmation")

        add(mainPanel)
        cardLayout.show(mainPanel, "Landing")
    }

    private fun createLandingPage(): JPanel {
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color(230, 240, 255)
            border = EmptyBorder(100, 200, 100, 200)
        }

        val title = JLabel("CareConnect").apply {
            font = Font("Arial", Font.BOLD, 48)
            alignmentX = Component.CENTER_ALIGNMENT
            foreground = Color(0x003366)
        }

        val description = JLabel(
            "<html><div style='text-align:center; font-size:20px; color:#333333;'>" +
                    "CareConnect helps you find caretakers for your loved ones—<br>" +
                    "whether children or elderly—right in your community.<br><br>" +
                    "Our mission: connect you with compassionate caregivers tailored to your needs." +
                    "</div></html>"
        ).apply {
            font = Font("Arial", Font.PLAIN, 22)
            alignmentX = Component.CENTER_ALIGNMENT
        }

        val continueButton = JButton("Continue").apply {
            font = Font("Arial", Font.BOLD, 28)
            foreground = Color.BLACK
            background = Color(0xA0C8FF)
            isOpaque = true
            isFocusPainted = false
            alignmentX = Component.CENTER_ALIGNMENT
            preferredSize = Dimension(220, 70)
            maximumSize = Dimension(220, 70)
            addActionListener { cardLayout.show(mainPanel, "Form") }
        }

        panel.add(Box.createVerticalGlue())
        panel.add(title)
        panel.add(Box.createRigidArea(Dimension(0, 40)))
        panel.add(description)
        panel.add(Box.createRigidArea(Dimension(0, 60)))
        panel.add(continueButton)
        panel.add(Box.createVerticalGlue())
        return panel
    }

    private fun createFormPage(): JPanel {
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = EmptyBorder(50, 200, 50, 200)
            background = Color(250, 250, 250)
        }

        fun createLabel(text: String) = JLabel(text).apply {
            font = Font("Arial", Font.BOLD, 22)
            alignmentX = Component.LEFT_ALIGNMENT
        }

        fun createTextField() = JTextField().apply {
            font = Font("Arial", Font.PLAIN, 20)
            maximumSize = Dimension(Int.MAX_VALUE, 40)
            alignmentX = Component.LEFT_ALIGNMENT
        }

        caretakerTypeBox = JComboBox(arrayOf("Children", "Elderly")).apply {
            font = Font("Arial", Font.PLAIN, 20)
            maximumSize = Dimension(Int.MAX_VALUE, 40)
            alignmentX = Component.LEFT_ALIGNMENT
        }

        nameField = createTextField()
        phoneField = createTextField()

        countryCombo = JComboBox(getCountryLocales()).apply {
            font = Font("Arial", Font.PLAIN, 20)
            maximumSize = Dimension(Int.MAX_VALUE, 40)
            alignmentX = Component.LEFT_ALIGNMENT
            renderer = ListCellRenderer { list, value, index, isSelected, cellHasFocus ->
                val label = JLabel(value.displayCountry).apply {
                    font = list.font
                    background = if (isSelected) list.selectionBackground else list.background
                    foreground = if (isSelected) list.selectionForeground else list.foreground
                    isOpaque = true
                }
                label
            }
        }

        addressField = createTextField()
        zipcodeField = createTextField()

        val submitPanel = JPanel(FlowLayout(FlowLayout.CENTER)).apply {
            background = Color(250, 250, 250)
        }

        val submitButton = JButton("Continue").apply {
            font = Font("Arial", Font.BOLD, 24)
            foreground = Color.BLACK // Changed from white to black
            background = Color(0x339933)
            isOpaque = true
            isFocusPainted = false
            preferredSize = Dimension(200, 60)
            maximumSize = Dimension(200, 60)
            addActionListener {
                if (validateForm()) {
                    cardLayout.show(mainPanel, "Confirmation")
                    isEnabled = false
                    sendFormDataToBackend(
                        caretakerTypeBox.selectedItem.toString(),
                        nameField.text.trim(),
                        phoneField.text.trim(),
                        (countryCombo.selectedItem as Locale).displayCountry,
                        addressField.text.trim(),
                        zipcodeField.text.trim(),
                        onSuccess = { SwingUtilities.invokeLater { isEnabled = true } },
                        onError = {
                            SwingUtilities.invokeLater {
                                JOptionPane.showMessageDialog(this@CareConnectApp, it, "Error", JOptionPane.ERROR_MESSAGE)
                                isEnabled = true
                            }
                        }
                    )
                } else {
                    JOptionPane.showMessageDialog(this@CareConnectApp,
                        "Please complete all fields correctly: Phone (10 digits), Zipcode (5 digits).",
                        "Validation Error", JOptionPane.ERROR_MESSAGE)
                }
            }
        }

        submitPanel.add(submitButton)

        fun addGap() = panel.add(Box.createRigidArea(Dimension(0, 25)))

        panel.add(createLabel("Who do you need caretaking for?"))
        panel.add(caretakerTypeBox); addGap()
        panel.add(createLabel("Your Name:"))
        panel.add(nameField); addGap()
        panel.add(createLabel("Phone Number:"))
        panel.add(phoneField); addGap()
        panel.add(createLabel("Country:"))
        panel.add(countryCombo); addGap()
        panel.add(createLabel("Address:"))
        panel.add(addressField); addGap()
        panel.add(createLabel("Zipcode:"))
        panel.add(zipcodeField); addGap()
        panel.add(submitPanel)

        return panel
    }

    private fun getCountryLocales(): Array<Locale> =
        Locale.getISOCountries()
            .map { Locale("", it) }
            .sortedBy { it.displayCountry }
            .toTypedArray()

    private fun validateForm(): Boolean {
        val name = nameField.text.trim()
        val phone = phoneField.text.trim()
        val zipcode = zipcodeField.text.trim()
        return name.isNotEmpty() && phone.matches(Regex("\\d{10}")) && zipcode.matches(Regex("\\d{5}"))
    }

    private fun createConfirmationPage(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color(230, 255, 230)
            border = EmptyBorder(100, 200, 100, 200)

            add(Box.createVerticalGlue())

            add(JLabel("Thank you!").apply {
                font = Font("Arial", Font.BOLD, 48)
                alignmentX = Component.CENTER_ALIGNMENT
                foreground = Color(0x006600)
            })

            add(Box.createRigidArea(Dimension(0, 30)))

            add(JLabel(
                "<html><div style='text-align:center; font-size:24px; color:#004400;'>" +
                        "We have received your information.<br><br>" +
                        "Our team will communicate with you shortly." +
                        "</div></html>"
            ).apply {
                font = Font("Arial", Font.PLAIN, 24)
                alignmentX = Component.CENTER_ALIGNMENT
                horizontalAlignment = SwingConstants.CENTER
            })

            add(Box.createVerticalGlue())
        }
    }

    private fun sendFormDataToBackend(
        caretakerType: String, name: String, phone: String,
        country: String, address: String, zipcode: String,
        onSuccess: () -> Unit, onError: (String) -> Unit
    ) {
        Thread {
            try {
                val url = URL("https://example.com/api/caretaker-request") // Replace with real backend
                val postData = """{"caretakerType":"$caretakerType","name":"$name","phone":"$phone","country":"$country","address":"$address","zipcode":"$zipcode"}"""
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json; utf-8")
                    doOutput = true
                    outputStream.use { it.write(postData.toByteArray(StandardCharsets.UTF_8)) }
                    if (responseCode in 200..201) onSuccess() else onError("Server responded: $responseCode")
                    disconnect()
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }.start()
    }
}

fun main() = SwingUtilities.invokeLater {
    CareConnectApp().apply { isVisible = true }
}
