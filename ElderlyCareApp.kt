import java.awt.*
import javax.swing.*

class ElderlyCareApp : JFrame() {
    private val cardLayout = CardLayout()
    private val mainPanel = JPanel(cardLayout)

    private lateinit var phoneField: JTextField
    private lateinit var caretakerTypeBox: JComboBox<String>
    private lateinit var locationField: JTextField

    init {
        title = "ElderCare - Global Care Solutions"
        setSize(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        // Add panels
        mainPanel.add(createLandingPage(), "Landing")
        mainPanel.add(createPhoneNumberPanel(), "PhoneNumber")
        mainPanel.add(createQuestionnairePanel(), "Questionnaire")
        mainPanel.add(createConfirmationPanel(), "Confirmation")

        add(mainPanel)
        cardLayout.show(mainPanel, "Landing")
    }

    private fun createLandingPage(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.background = Color(230, 240, 255) // Soft light blue background
        panel.border = BorderFactory.createEmptyBorder(40, 60, 40, 60)

        val title = JLabel("<html><h1 style='color:#003366;'>Welcome to <span style='color:#0066CC;'>ElderCare</span></h1></html>").apply {
            alignmentX = Component.CENTER_ALIGNMENT
            font = Font("SansSerif", Font.BOLD, 36)
            horizontalAlignment = SwingConstants.CENTER
        }

        val description = JLabel(
            """
            <html>
                <div style='text-align: center; font-size:16px; color:#333333; width:600px;'>
                    <p>At <b style='color:#0066CC;'>ElderCare</b>, we provide compassionate, personalized care for anyone, 
                    <span style='color:#007700; font-weight:bold;'>all around the world</span>.</p>
                    <p>Whether you're in a busy city or a quiet village, we help connect you with 
                    <span style='color:#993333;'>trusted, qualified caretakers</span> who fit your needs and location.</p>
                    <p>We serve families across countries and cultures, offering services from nurses to companions, housekeepers, and more.</p>
                    <p>Let us help you find the <span style='color:#8844AA;'>perfect caregiver</span> for your loved one — wherever you are.</p>
                </div>
            </html>
            """.trimIndent()
        ).apply {
            alignmentX = Component.CENTER_ALIGNMENT
            font = Font("SansSerif", Font.PLAIN, 16)
            horizontalAlignment = SwingConstants.CENTER
        }

        val continueButton = JButton("Continue").apply {
            font = Font("SansSerif", Font.BOLD, 20)
            preferredSize = Dimension(180, 50)
            alignmentX = Component.CENTER_ALIGNMENT
            background = Color(0, 102, 204)
            foreground = Color.WHITE
            isFocusPainted = false
            addActionListener {
                cardLayout.show(mainPanel, "PhoneNumber")
            }
        }

        panel.add(Box.createVerticalGlue())
        panel.add(title)
        panel.add(Box.createRigidArea(Dimension(0, 30)))
        panel.add(description)
        panel.add(Box.createRigidArea(Dimension(0, 40)))
        panel.add(continueButton)
        panel.add(Box.createVerticalGlue())

        return panel
    }

    private fun createPhoneNumberPanel(): JPanel {
        val panel = JPanel(GridLayout(3, 1, 10, 10)).apply {
            border = BorderFactory.createEmptyBorder(100, 150, 100, 150)
        }

        val label = JLabel("Enter your phone number:").apply {
            font = Font("SansSerif", Font.PLAIN, 20)
        }

        phoneField = JTextField().apply {
            font = Font("SansSerif", Font.PLAIN, 20)
        }

        val nextButton = JButton("Next").apply {
            font = Font("SansSerif", Font.BOLD, 20)
            addActionListener {
                val phone = phoneField.text.trim()
                if (phone.matches(Regex("\\d{10}"))) {
                    cardLayout.show(mainPanel, "Questionnaire")
                } else {
                    JOptionPane.showMessageDialog(this@ElderlyCareApp, "Please enter a valid 10-digit phone number.")
                }
            }
        }

        panel.add(label)
        panel.add(phoneField)
        panel.add(nextButton)

        return panel
    }

    private fun createQuestionnairePanel(): JPanel {
        val panel = JPanel(GridLayout(5, 1, 15, 15)).apply {
            border = BorderFactory.createEmptyBorder(100, 150, 100, 150)
        }

        val caretakerLabel = JLabel("What type of caretaker do you need?").apply {
            font = Font("SansSerif", Font.PLAIN, 20)
        }

        caretakerTypeBox = JComboBox(arrayOf("Nurse", "Companion", "Housekeeper", "Physical Therapist")).apply {
            font = Font("SansSerif", Font.PLAIN, 20)
        }

        val locationLabel = JLabel("Enter your location (city or zip):").apply {
            font = Font("SansSerif", Font.PLAIN, 20)
        }

        locationField = JTextField().apply {
            font = Font("SansSerif", Font.PLAIN, 20)
        }

        val submitButton = JButton("Submit").apply {
            font = Font("SansSerif", Font.BOLD, 20)
            addActionListener {
                cardLayout.show(mainPanel, "Confirmation")
            }
        }

        panel.add(caretakerLabel)
        panel.add(caretakerTypeBox)
        panel.add(locationLabel)
        panel.add(locationField)
        panel.add(submitButton)

        return panel
    }

    private fun createConfirmationPanel(): JPanel {
        val panel = JPanel(BorderLayout()).apply {
            background = Color(245, 255, 245)
        }

        val label = JLabel(
            """<html>
                <div style='text-align: center; font-size: 18px; color: #333333;'>
                    <h2 style='color:#006600;'>Thank you!</h2>
                    <p>We are now matching you with the best available caregiver near your location.</p>
                    <p>Our team will contact you at the number you provided shortly.</p>
                </div>
               </html>"""
        ).apply {
            font = Font("SansSerif", Font.PLAIN, 20)
            horizontalAlignment = SwingConstants.CENTER
        }

        panel.add(label, BorderLayout.CENTER)
        return panel
    }
}

fun main() {
    SwingUtilities.invokeLater {
        ElderlyCareApp().isVisible = true
    }
}
